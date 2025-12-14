package com.banco.ms_banco.mapper;

import org.springframework.stereotype.Component;

import com.banco.ms_banco.dto.clientes.ClienteRequestDTO;
import com.banco.ms_banco.dto.clientes.ClienteResponseDTO;
import com.banco.ms_banco.dto.clientes.PersonaDTO;
import com.banco.ms_banco.model.Cliente;
import com.banco.ms_banco.model.Persona;

@Component
public class ClienteMapper {

    // Persona Mappings
    public Persona toEntity(PersonaDTO dto) {
        if (dto == null) return null;
        
        Persona entity = new Persona();
        entity.setPersonaId(dto.getPersonaId());
        entity.setIdentificacion(dto.getIdentificacion());
        entity.setNombre(dto.getNombre());
        entity.setGenero(dto.getGenero());
        entity.setEdad(dto.getEdad());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        return entity;
    }

    public PersonaDTO toDTO(Persona entity) {
        if (entity == null) return null;
        
        PersonaDTO dto = PersonaDTO.builder()
        .personaId(entity.getPersonaId())
        .identificacion(entity.getIdentificacion())
        .nombre(entity.getNombre())
        .genero(entity.getGenero())
        .edad(entity.getEdad())
        .direccion(entity.getDireccion())
        .telefono(entity.getTelefono())
        .build();
        
        return dto;
    }

    // DTO to Entity
    public Cliente toEntity(ClienteRequestDTO dto) {
        if (dto == null) return null;
        
        Cliente entity = new Cliente();
        entity.setContrasena(dto.getContrasena());
        entity.setPersona(toEntity(dto.getPersona()));
        return entity;
    }

    public ClienteResponseDTO toResponseDTO(Cliente entity) {
        if (entity == null) return null;

        ClienteResponseDTO dto = ClienteResponseDTO.builder()
                .clienteId(entity.getClienteId())
                .estado(entity.getEstado())
                .persona(toDTO(entity.getPersona()))
                .build();
        return dto;
    }
}