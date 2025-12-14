package com.banco.ms_banco.service.Impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.ms_banco.dto.movimientos.MovimientoRequestDTO;
import com.banco.ms_banco.dto.movimientos.MovimientoResponseDTO;
import com.banco.ms_banco.exception.CuentaInactivaException;
import com.banco.ms_banco.exception.CuentaNotFoundException;
import com.banco.ms_banco.exception.MovimientoInvalidoException;
import com.banco.ms_banco.exception.SaldoInsuficienteException;
import com.banco.ms_banco.mapper.MovimientoMapper;
import com.banco.ms_banco.model.Cuenta;
import com.banco.ms_banco.model.Movimiento;
import com.banco.ms_banco.repository.CuentaRepository;
import com.banco.ms_banco.repository.MovimientoRepository;
import com.banco.ms_banco.service.MovimientoService;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoService.class);

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoMapper movimientoMapper;

    @Override
    public MovimientoResponseDTO registrarMovimiento(String numeroCuenta, MovimientoRequestDTO movimientoRequest) {
        try {
            logger.info("Registrando movimiento para cuenta: {} - Valor recibido: {}", 
                    numeroCuenta, movimientoRequest.getValor());

            validarMovimiento(movimientoRequest);

            Cuenta cuenta = obtenerYValidarCuenta(numeroCuenta);

            String tipoMovimiento = determinarTipoMovimiento(movimientoRequest.getValor());
            
            BigDecimal valorAbsoluto = movimientoRequest.getValor().abs();

            BigDecimal saldoActual = calcularSaldoDisponible(cuenta.getCuentaId());
            
            if ("RETIRO".equals(tipoMovimiento)) {
                validarRetiro(saldoActual, valorAbsoluto);
            }

            BigDecimal nuevoSaldo = calcularNuevoSaldo(tipoMovimiento, saldoActual, valorAbsoluto);

            Movimiento movimiento = crearMovimiento(tipoMovimiento, valorAbsoluto, nuevoSaldo, cuenta);
            Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

            logger.info("Movimiento {} registrado exitosamente para cuenta: {}. Valor: {}, Nuevo saldo: {}", 
                        tipoMovimiento, numeroCuenta, valorAbsoluto, nuevoSaldo);

            return crearResponseDTO(movimientoGuardado, movimientoRequest.getValor());

        } catch (CuentaNotFoundException | SaldoInsuficienteException | 
                CuentaInactivaException | MovimientoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar movimiento para cuenta ID: {}", numeroCuenta, e);
            throw new RuntimeException("Error al registrar el movimiento: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosPorCuenta(Long cuentaId) {
        try {
            logger.info("Obteniendo movimientos para cuenta ID: {}", cuentaId);
            
            if (!cuentaRepository.existsById(cuentaId)) {
                throw new CuentaNotFoundException(cuentaId);
            }
            
            return movimientoRepository.findByCuentaCuentaIdOrderByFechaDesc(cuentaId)
                    .stream()
                    .map(movimientoMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (CuentaNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al obtener movimientos para cuenta ID: {}", cuentaId, e);
            throw new RuntimeException("Error al obtener los movimientos de la cuenta");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosPorCliente(Long clienteId) {
        try {
            logger.info("Obteniendo movimientos para cliente ID: {}", clienteId);
            return movimientoRepository.findByCuentaClienteId(clienteId)
                    .stream()
                    .map(movimientoMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener movimientos para cliente ID: {}", clienteId, e);
            throw new RuntimeException("Error al obtener los movimientos del cliente");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponseDTO obtenerMovimientoPorId(Long movimientoId) {
        try {
            logger.info("Obteniendo movimiento con ID: {}", movimientoId);
            Movimiento movimiento = movimientoRepository.findById(movimientoId)
                    .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + movimientoId));
            return movimientoMapper.toResponseDTO(movimiento);
        } catch (Exception e) {
            logger.error("Error al obtener movimiento por ID: {}", movimientoId, e);
            throw new RuntimeException("Error al obtener el movimiento");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerTodosLosMovimientos() {
        try {
            logger.info("Obteniendo todos los movimientos");
            return movimientoRepository.findAll()
                    .stream()
                    .map(movimientoMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener todos los movimientos", e);
            throw new RuntimeException("Error al obtener los movimientos");
        }
    }

    @Override
    public BigDecimal calcularSaldoDisponible(Long cuentaId) {
        try {
            Optional<Movimiento> ultimoMovimiento = this.movimientoRepository
                    .findTopByCuentaCuentaIdOrderByFechaDesc(cuentaId);
            
            return ultimoMovimiento
                    .map(Movimiento::getSaldo)
                    .orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            logger.error("Error al calcular saldo para cuenta ID: {}", cuentaId, e);
            throw new RuntimeException("Error al calcular el saldo disponible");
        }
    }

    @Override
    public void crearMovimientoInicial(Cuenta cuenta) {
        try {
            Movimiento movimientoInicial = new Movimiento(
                "DEPOSITO",
                cuenta.getSaldoInicial(),
                cuenta.getSaldoInicial(),
                cuenta
            );
            movimientoRepository.save(movimientoInicial);
            logger.info("Movimiento inicial registrado para cuenta: {}", cuenta.getNumeroCuenta());
        } catch (Exception e) {
            logger.error("Error al crear movimiento inicial para cuenta: {}", cuenta.getNumeroCuenta(), e);
            throw new RuntimeException("Error al crear movimiento inicial");
        }
    }

    private void validarMovimiento(MovimientoRequestDTO movimientoRequest) {
        if (movimientoRequest.getValor() == null) {
            throw new MovimientoInvalidoException("El valor del movimiento es obligatorio");
        }

        if (movimientoRequest.getValor().compareTo(BigDecimal.ZERO) == 0) {
            throw new MovimientoInvalidoException("El valor del movimiento no puede ser cero");
        }

        if (movimientoRequest.getValor().abs().compareTo(new BigDecimal("0.01")) < 0) {
            throw new MovimientoInvalidoException("El valor mínimo del movimiento es 0.01");
        }

        if (movimientoRequest.getValor().abs().compareTo(new BigDecimal("1000000")) > 0) {
            throw new MovimientoInvalidoException("El valor máximo del movimiento es 1,000,000.00");
        }
    }

    private String determinarTipoMovimiento(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            return "DEPOSITO";
        } else {
            return "RETIRO";
        }
    }


    private Cuenta obtenerYValidarCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException(numeroCuenta, true));

        if (!"ACTIVA".equals(cuenta.getEstado())) {
            throw new CuentaInactivaException(cuenta.getNumeroCuenta(), cuenta.getEstado());
        }

        return cuenta;
    }

    private void validarRetiro(BigDecimal saldoActual, BigDecimal valorRetiro) {
        if (saldoActual.compareTo(valorRetiro) < 0) {
            throw new SaldoInsuficienteException(saldoActual, valorRetiro);
        }
    }


    private BigDecimal calcularNuevoSaldo(String tipoMovimiento, BigDecimal saldoActual, BigDecimal valorAbsoluto) {
        if ("DEPOSITO".equals(tipoMovimiento)) {
            return saldoActual.add(valorAbsoluto);
        } else {
            return saldoActual.subtract(valorAbsoluto);
        }
    }

    private Movimiento crearMovimiento(String tipoMovimiento, BigDecimal valorAbsoluto, 
                                    BigDecimal nuevoSaldo, Cuenta cuenta) {
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(valorAbsoluto);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);
        return movimiento;
    }

    private MovimientoResponseDTO crearResponseDTO(Movimiento movimiento, BigDecimal valorOriginal) {
        MovimientoResponseDTO dto = new MovimientoResponseDTO();
        dto.setMovimientoId(movimiento.getMovimientoId());
        dto.setFecha(movimiento.getFecha());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setValor(valorOriginal);
        dto.setSaldo(movimiento.getSaldo());
        dto.setCuentaId(movimiento.getCuenta().getCuentaId());
        dto.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
        return dto;
    }
}
