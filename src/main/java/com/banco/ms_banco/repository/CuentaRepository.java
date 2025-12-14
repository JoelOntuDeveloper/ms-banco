package com.banco.ms_banco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.banco.ms_banco.model.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    @Query("SELECT c FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta")
    Optional<Cuenta> findByNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);
    
    @Query("SELECT c FROM Cuenta c WHERE c.clienteId = :clienteId")
    List<Cuenta> findByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT c FROM Cuenta c WHERE c.estado = :estado")
    List<Cuenta> findByEstado(@Param("estado") String estado);
    
    @Query("SELECT COUNT(c) > 0 FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta")
    boolean existsByNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);

    List<Cuenta> findByClienteIdAndEstado(Long clienteId, String estado);
}
