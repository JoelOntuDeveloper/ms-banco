package com.banco.ms_banco.service.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import com.banco.ms_banco.repository.ClienteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.ms_banco.dto.reportes.EstadoCuentaReporteDTO;
import com.banco.ms_banco.exception.CuentasNoEncontradasException;
import com.banco.ms_banco.exception.FechaInvalidaException;
import com.banco.ms_banco.model.Movimiento;
import com.banco.ms_banco.repository.CuentaRepository;
import com.banco.ms_banco.repository.custom.CustomMovimientoRepository;
import com.banco.ms_banco.service.MovimientoService;
import com.banco.ms_banco.service.ReporteService;
import com.banco.ms_banco.dto.FileBase64DTO;
import com.banco.ms_banco.dto.cuentas.CuentaResumenDTO;
import com.banco.ms_banco.dto.movimientos.MovimientoDetalleDTO;
import com.banco.ms_banco.model.Cuenta;
import com.banco.ms_banco.model.Cliente;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CustomMovimientoRepository customMovimientoRepository;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private ClienteRepository clienteRepository;

    public EstadoCuentaReporteDTO generarReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            logger.info("Generando reporte de estado de cuenta para cliente: {} entre {} y {}", 
                        clienteId, fechaInicio, fechaFin);

            validarFechas(fechaInicio, fechaFin);

            List<Cuenta> cuentas = cuentaRepository.findByClienteIdAndEstado(clienteId, "ACTIVA");
            
            if (cuentas.isEmpty()) {
                logger.warn("No se encontraron cuentas activas para el cliente: {}", clienteId);
                throw CuentasNoEncontradasException.paraCliente(clienteId);
            }

            LocalDateTime fechaInicioTime = fechaInicio.atStartOfDay();
            LocalDateTime fechaFinTime = fechaFin.atTime(LocalTime.MAX);

            List<Movimiento> movimientos = customMovimientoRepository
                    .findMovimientosByClienteAndFechaRange(clienteId, fechaInicioTime, fechaFinTime);

            if (movimientos.isEmpty()) {
                logger.warn("No se encontraron movimientos para el cliente {} en el rango {} - {}", 
                            clienteId, fechaInicio, fechaFin);
                throw CuentasNoEncontradasException.paraClienteEnRango(
                    clienteId, fechaInicio.toString(), fechaFin.toString());
            }

            Map<Long, List<Movimiento>> movimientosPorCuenta = movimientos.stream()
                    .collect(Collectors.groupingBy(m -> m.getCuenta().getCuentaId()));

            List<CuentaResumenDTO> cuentasDTO = cuentas.stream()
                    .map(cuenta -> {
                        CuentaResumenDTO cuentaDTO = convertirCuentaADTO(cuenta);
                        
                        List<Movimiento> movimientosCuenta = movimientosPorCuenta.getOrDefault(cuenta.getCuentaId(), new ArrayList<>());
                        List<MovimientoDetalleDTO> movimientosDTO = movimientosCuenta.stream()
                                .map(this::convertirMovimientoADTO)
                                .collect(Collectors.toList());
                        
                        cuentaDTO.setMovimientos(movimientosDTO);
                        return cuentaDTO;
                    })
                    .collect(Collectors.toList());

            if (cuentasDTO.isEmpty()) {
                throw CuentasNoEncontradasException.paraClienteEnRango(
                    clienteId, fechaInicio.toString(), fechaFin.toString());
            }

            EstadoCuentaReporteDTO reporte = new EstadoCuentaReporteDTO(clienteId, fechaInicio, fechaFin, cuentasDTO);
            
            logger.info("Reporte generado exitosamente para cliente: {}. Total cuentas: {}, Saldo total: {}", 
                        clienteId, cuentasDTO.size(), reporte.getSaldoTotal());

            return reporte;

        } catch (FechaInvalidaException | CuentasNoEncontradasException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al generar reporte de estado de cuenta para cliente: {}", clienteId, e);
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage());
        }
    }

    private CuentaResumenDTO convertirCuentaADTO(Cuenta cuenta) {
        try {
            if (!"ACTIVA".equals(cuenta.getEstado())) {
                throw CuentasNoEncontradasException.cuentaInactiva(cuenta.getCuentaId(), cuenta.getEstado());
            }
            
            BigDecimal saldoActual = movimientoService.calcularSaldoDisponible(cuenta.getCuentaId());
            
            return CuentaResumenDTO.builder()
                .cuentaId(cuenta.getCuentaId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .estado(cuenta.getEstado())
                .saldoActual(saldoActual)
                .build();

        } catch (CuentasNoEncontradasException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al convertir cuenta a DTO: {}", cuenta.getCuentaId(), e);
            return CuentaResumenDTO.builder()
            .cuentaId(cuenta.getCuentaId())
            .numeroCuenta(cuenta.getNumeroCuenta())
            .tipoCuenta(cuenta.getTipoCuenta())
            .estado(cuenta.getEstado())
            .saldoActual(cuenta.getSaldoInicial())
            .build();
        }
    }

    private MovimientoDetalleDTO convertirMovimientoADTO(Movimiento movimiento) {
        return new MovimientoDetalleDTO(
            movimiento.getMovimientoId(),
            movimiento.getFecha(),
            movimiento.getTipoMovimiento(),
            movimiento.getValor(),
            movimiento.getTipoMovimiento().equals("DEBITO") ? 
            "Debito de $" + movimiento.getValor() : "Crédito de $" + movimiento.getValor(),
            movimiento.getSaldo()
        );
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw FechaInvalidaException.fechasNulas();
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw FechaInvalidaException.fechaInicioPosterior();
        }

        if (fechaInicio.plusYears(1).isBefore(fechaFin)) {
            throw FechaInvalidaException.rangoExcesivo();
        }

        LocalDate hoy = LocalDate.now();
        if (fechaInicio.isAfter(hoy) || fechaFin.isAfter(hoy)) {
            throw FechaInvalidaException.fechasFuturas();
        }
    }

    public void validarFechas(String fechaInicioStr, String fechaFinStr) {
        try {
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);
            validarFechas(fechaInicio, fechaFin);
        } catch (DateTimeParseException e) {
            throw FechaInvalidaException.formatoInvalido();
        }
    }
    
    public FileBase64DTO generarReporteEstadoCuentaPDF(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            EstadoCuentaReporteDTO reporte = generarReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);

            Cliente cliente = clienteRepository.findById(clienteId).orElse(null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            document.add(new Paragraph("Estado de Cuenta", titleFont));
            String clienteInfo = "Cliente ID: " + reporte.getClienteId();
            if (cliente != null && cliente.getPersona() != null) {
                clienteInfo = cliente.getPersona().getNombre() + " - " + cliente.getPersona().getIdentificacion();
            }
            document.add(new Paragraph(clienteInfo));
            document.add(new Paragraph("Periodo: " + fechaInicio + " - " + fechaFin));
            document.add(new Paragraph(" "));

            for (CuentaResumenDTO cuentaDTO : reporte.getCuentas()) {
                Cuenta cuenta = cuentaRepository.findById(cuentaDTO.getCuentaId()).orElse(null);

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.addCell(new PdfPCell(new Paragraph("Número de cuenta", headerFont)));
                table.addCell(cuentaDTO.getNumeroCuenta());
                table.addCell(new PdfPCell(new Paragraph("Tipo de cuenta", headerFont)));
                table.addCell(cuentaDTO.getTipoCuenta());
                table.addCell(new PdfPCell(new Paragraph("Saldo inicial", headerFont)));
                table.addCell(cuenta != null && cuenta.getSaldoInicial() != null ? cuenta.getSaldoInicial().toString() : "N/A");
                table.addCell(new PdfPCell(new Paragraph("Estado", headerFont)));
                table.addCell(cuentaDTO.getEstado());
                document.add(table);

                document.add(new Paragraph("Movimientos:"));

                PdfPTable mtable = new PdfPTable(5);
                mtable.setWidthPercentage(100);
                mtable.addCell(new PdfPCell(new Paragraph("Fecha", headerFont)));
                mtable.addCell(new PdfPCell(new Paragraph("Tipo", headerFont)));
                mtable.addCell(new PdfPCell(new Paragraph("Valor", headerFont)));
                mtable.addCell(new PdfPCell(new Paragraph("Descripción", headerFont)));
                mtable.addCell(new PdfPCell(new Paragraph("Saldo", headerFont)));

                if (cuentaDTO.getMovimientos() != null) {
                    for (MovimientoDetalleDTO m : cuentaDTO.getMovimientos()) {
                        mtable.addCell(m.getFecha() != null ? m.getFecha().toString() : "");
                        mtable.addCell(m.getTipoMovimiento() != null ? m.getTipoMovimiento() : "");
                        mtable.addCell(m.getValor() != null ? m.getValor().toString() : "");
                        mtable.addCell(m.getDescripcion() != null ? m.getDescripcion() : "");
                        mtable.addCell(m.getSaldoDespues() != null ? m.getSaldoDespues().toString() : "");
                    }
                }

                document.add(mtable);
                document.add(new Paragraph(" "));
            }

            document.close();

            byte[] pdfBytes = baos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(pdfBytes);

            String fileName = "estado_cuenta_" + (reporte.getCuentas().isEmpty() ? "sin_cuenta" : reporte.getCuentas().get(0).getNumeroCuenta()) + ".pdf";

            return new FileBase64DTO(fileName, "application/pdf", base64);

        } catch (Exception e) {
            logger.error("Error al generar reporte PDF para cliente: {}", clienteId, e);
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage());
        }
    }
    
}
