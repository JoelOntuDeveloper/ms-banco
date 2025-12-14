package com.banco.ms_banco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.banco.ms_banco.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    @Query("SELECT c FROM Cliente c WHERE c.persona.identificacion = :identificacion")
    Optional<Cliente> findByPersonaIdentificacion(@Param("identificacion") String identificacion);
    
    @Query("SELECT c FROM Cliente c WHERE c.estado = :estado")
    List<Cliente> findByEstado(@Param("estado") String estado);
    
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.persona.identificacion = :identificacion")
    boolean existsByPersonaIdentificacion(@Param("identificacion") String identificacion);
}
