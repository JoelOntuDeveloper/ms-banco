package com.banco.ms_banco.service;

import java.math.BigDecimal;
import java.util.List;

import com.banco.ms_banco.dto.movimientos.MovimientoRequestDTO;
import com.banco.ms_banco.dto.movimientos.MovimientoResponseDTO;
import com.banco.ms_banco.model.Cuenta;

public interface MovimientoService {

    MovimientoResponseDTO registrarMovimiento(String numeroCuenta, MovimientoRequestDTO movimientoRequest);
    List<MovimientoResponseDTO> obtenerMovimientosPorCuenta(Long cuentaId);
    List<MovimientoResponseDTO> obtenerMovimientosPorCliente(Long clienteId);
    MovimientoResponseDTO obtenerMovimientoPorId(Long movimientoId);
    List<MovimientoResponseDTO> obtenerTodosLosMovimientos();
    BigDecimal calcularSaldoDisponible(Long cuentaId);
    void crearMovimientoInicial(Cuenta cuenta);
}