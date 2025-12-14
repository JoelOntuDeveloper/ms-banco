package com.banco.ms_banco.repository.custom;

import java.time.LocalDateTime;
import java.util.List;

import com.banco.ms_banco.model.Movimiento;

public interface CustomMovimientoRepository {
    
    List<Movimiento> findMovimientosByClienteAndFechaRange(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
