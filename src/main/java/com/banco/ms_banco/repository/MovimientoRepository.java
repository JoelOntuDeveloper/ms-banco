package com.banco.ms_banco.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.banco.ms_banco.model.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cuentaId = :cuentaId ORDER BY m.fecha DESC")
    List<Movimiento> findByCuentaCuentaIdOrderByFechaDesc(@Param("cuentaId") Long cuentaId);
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cuentaId = :cuentaId ORDER BY m.fecha DESC LIMIT 1")
    Optional<Movimiento> findTopByCuentaCuentaIdOrderByFechaDesc(@Param("cuentaId") Long cuentaId);
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.clienteId = :clienteId")
    List<Movimiento> findByCuentaClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT COUNT(m) > 0 FROM Movimiento m WHERE m.cuenta.cuentaId = :cuentaId AND m.fecha > :fechaLimite")
    boolean existsByCuentaCuentaIdAndFechaAfter(@Param("cuentaId") Long cuentaId, 
                                                @Param("fechaLimite") LocalDateTime fechaLimite);
}
