package com.banco.ms_banco.service;

import java.time.LocalDate;

import com.banco.ms_banco.dto.FileBase64DTO;
import com.banco.ms_banco.dto.reportes.EstadoCuentaReporteDTO;

public interface ReporteService {

    EstadoCuentaReporteDTO generarReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
    FileBase64DTO generarReporteEstadoCuentaPDF(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
    void validarFechas(String fechaInicioStr, String fechaFinStr);
    
}
