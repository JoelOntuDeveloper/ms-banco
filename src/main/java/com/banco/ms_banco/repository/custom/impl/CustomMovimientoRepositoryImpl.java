package com.banco.ms_banco.repository.custom.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.banco.ms_banco.model.Movimiento;
import com.banco.ms_banco.repository.custom.CustomMovimientoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class CustomMovimientoRepositoryImpl implements CustomMovimientoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Movimiento> findMovimientosByClienteAndFechaRange(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String jpql = """
            SELECT m FROM Movimiento m 
            JOIN m.cuenta c 
            WHERE c.clienteId = :clienteId 
            AND m.fecha BETWEEN :fechaInicio AND :fechaFin 
            ORDER BY m.fecha DESC
            """;

        TypedQuery<Movimiento> query = entityManager.createQuery(jpql, Movimiento.class);
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        return query.getResultList();
    }
}
