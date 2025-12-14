package com.banco.ms_banco.service;

import java.math.BigDecimal;
import java.util.List;

import com.banco.ms_banco.dto.cuentas.CuentaRequestDTO;
import com.banco.ms_banco.dto.cuentas.CuentaResponseDTO;

public interface CuentaService {
    
    CuentaResponseDTO crearCuenta(CuentaRequestDTO cuentaRequest);
    List<CuentaResponseDTO> obtenerCuentasPorCliente(Long clienteId);
    CuentaResponseDTO obtenerCuentaPorId(Long cuentaId);
    CuentaResponseDTO obtenerCuentaPorNumero(String numeroCuenta);
    List<CuentaResponseDTO> obtenerTodasLasCuentas();
    BigDecimal consultarSaldo(Long cuentaId);
    CuentaResponseDTO actualizarEstadoCuenta(Long cuentaId, String nuevoEstado);
    CuentaResponseDTO crearCuentaAutomatica(Long clienteId, String nombreCliente);
    CuentaResponseDTO eliminarCuentaLogicamente(Long cuentaId);
    CuentaResponseDTO reactivarCuenta(Long cuentaId);
}
