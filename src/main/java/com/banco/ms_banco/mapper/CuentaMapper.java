package com.banco.ms_banco.mapper;

import org.springframework.stereotype.Component;

import com.banco.ms_banco.dto.cuentas.CuentaRequestDTO;
import com.banco.ms_banco.dto.cuentas.CuentaResponseDTO;
import com.banco.ms_banco.model.Cuenta;

@Component
public class CuentaMapper {

    public Cuenta toEntity(CuentaRequestDTO dto) {
        if (dto == null) return null;
        
        Cuenta entity = new Cuenta();
        entity.setTipoCuenta(dto.getTipoCuenta());
        entity.setSaldoInicial(dto.getSaldoInicial());
        entity.setClienteId(dto.getClienteId());
        return entity;
    }

    public CuentaResponseDTO toResponseDTO(Cuenta entity) {
        if (entity == null) return null;
        
        CuentaResponseDTO dto = CuentaResponseDTO.builder()
        .cuentaId(entity.getCuentaId())
        .numeroCuenta(entity.getNumeroCuenta())
        .tipoCuenta(entity.getTipoCuenta())
        .saldoInicial(entity.getSaldoInicial())
        .estado(entity.getEstado())
        .clienteId(entity.getClienteId())
        .build();
        
        return dto;
    }
}