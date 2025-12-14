package com.banco.ms_banco.dto.reportes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.banco.ms_banco.dto.cuentas.CuentaResumenDTO;

import lombok.Data;

@Data
public class EstadoCuentaReporteDTO {
    private Long clienteId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CuentaResumenDTO> cuentas;
    private BigDecimal saldoTotal;

    public EstadoCuentaReporteDTO() {}

    public EstadoCuentaReporteDTO(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin, 
                                List<CuentaResumenDTO> cuentas) {
        this.clienteId = clienteId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cuentas = cuentas;
        this.saldoTotal = calcularSaldoTotal();
    }

    private BigDecimal calcularSaldoTotal() {
        if (cuentas == null || cuentas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cuentas.stream()
                .map(CuentaResumenDTO::getSaldoActual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}