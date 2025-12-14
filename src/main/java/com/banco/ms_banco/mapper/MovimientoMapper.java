package com.banco.ms_banco.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.banco.ms_banco.dto.movimientos.MovimientoRequestDTO;
import com.banco.ms_banco.dto.movimientos.MovimientoResponseDTO;
import com.banco.ms_banco.model.Cuenta;
import com.banco.ms_banco.model.Movimiento;

@Component
public class MovimientoMapper {

    public Movimiento toEntity(MovimientoRequestDTO dto, Cuenta cuenta) {
        Movimiento entity = new Movimiento();
        entity.setCuenta(cuenta);
        return entity;
    }

    public MovimientoResponseDTO toResponseDTO(Movimiento entity) {
        if (entity == null) return null;
        
        MovimientoResponseDTO dto = new MovimientoResponseDTO();
        dto.setMovimientoId(entity.getMovimientoId());
        dto.setFecha(entity.getFecha());
        dto.setTipoMovimiento(entity.getTipoMovimiento());
        
        BigDecimal valorRespuesta = entity.getValor();
        if ("RETIRO".equals(entity.getTipoMovimiento())) {
            valorRespuesta = entity.getValor().negate();
        }
        dto.setValor(valorRespuesta);
        
        dto.setSaldo(entity.getSaldo());
        dto.setCuentaId(entity.getCuenta().getCuentaId());
        dto.setNumeroCuenta(entity.getCuenta().getNumeroCuenta());
        return dto;
    }
}