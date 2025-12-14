package com.banco.ms_banco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.banco.ms_banco.model.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    @Query("SELECT p FROM Persona p WHERE p.identificacion = :identificacion")
    Optional<Persona> findByIdentificacion(@Param("identificacion") String identificacion);
    
    @Query("SELECT COUNT(p) > 0 FROM Persona p WHERE p.identificacion = :identificacion")
    boolean existsByIdentificacion(@Param("identificacion") String identificacion);
}
