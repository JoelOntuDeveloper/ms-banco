package com.banco.ms_banco.dto.cuentas;

import java.math.BigDecimal;
import java.util.List;

import com.banco.ms_banco.dto.movimientos.MovimientoDetalleDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaResumenDTO {
    private Long cuentaId;
    private String numeroCuenta;
    private String tipoCuenta;
    private String estado;
    private BigDecimal saldoActual;
    private List<MovimientoDetalleDTO> movimientos;
}