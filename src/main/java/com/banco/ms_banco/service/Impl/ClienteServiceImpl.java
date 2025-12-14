package com.banco.ms_banco.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.ms_banco.dto.clientes.ClienteRequestDTO;
import com.banco.ms_banco.dto.clientes.ClienteResponseDTO;
import com.banco.ms_banco.dto.clientes.ClienteUpdateRequestDTO;
import com.banco.ms_banco.exception.ClienteNotFoundException;
import com.banco.ms_banco.exception.PersonaAlreadyExistsException;
import com.banco.ms_banco.exception.ValidationException;
import com.banco.ms_banco.mapper.ClienteMapper;
import com.banco.ms_banco.model.Cliente;
import com.banco.ms_banco.model.Persona;
import com.banco.ms_banco.repository.ClienteRepository;
import com.banco.ms_banco.repository.PersonaRepository;
import com.banco.ms_banco.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private ClienteMapper clienteMapper;

    @Override
    public ClienteResponseDTO crearCliente(ClienteRequestDTO clienteRequest) {
        logger.info("Iniciando creación de cliente: {}", clienteRequest.getPersona().getNombre());

        try {
            if (personaRepository.existsByIdentificacion(clienteRequest.getPersona().getIdentificacion())) {
                throw new PersonaAlreadyExistsException("identificación", clienteRequest.getPersona().getIdentificacion());
            }

            if (clienteRequest.getContrasena() == null || clienteRequest.getContrasena().trim().isEmpty()) {
                throw new ValidationException("La contraseña no puede estar vacía");
            }

            if (clienteRequest.getContrasena().length() < 6) {
                throw new ValidationException("La contraseña debe tener al menos 6 caracteres");
            }

            Cliente cliente = clienteMapper.toEntity(clienteRequest);

            Cliente clienteGuardado = clienteRepository.save(cliente);

            return clienteMapper.toResponseDTO(clienteGuardado);

        } catch (PersonaAlreadyExistsException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al crear cliente", e);
            throw new RuntimeException("Error inesperado al crear el cliente: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerTodosLosClientes() {
        try {
            return clienteRepository.findAll()
                    .stream()
                    .map(clienteMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener todos los clientes", e);
            throw new RuntimeException("Error al obtener la lista de clientes");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteResponseDTO> obtenerClientePorId(Long id) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente.isEmpty()) {
                throw new ClienteNotFoundException(id);
            }
            return cliente.map(clienteMapper::toResponseDTO);
        } catch (ClienteNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al obtener cliente por ID: {}", id, e);
            throw new RuntimeException("Error al obtener el cliente");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteResponseDTO> obtenerClientePorIdentificacion(String identificacion) {
        try {
            Optional<Cliente> cliente = clienteRepository.findByPersonaIdentificacion(identificacion);
            if (cliente.isEmpty()) {
                throw new ClienteNotFoundException(identificacion, true);
            }
            return cliente.map(clienteMapper::toResponseDTO);
        } catch (ClienteNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al obtener cliente por identificación: {}", identificacion, e);
            throw new RuntimeException("Error al obtener el cliente por identificación");
        }
    }

    @Override
    public ClienteResponseDTO actualizarCliente(Long id, ClienteUpdateRequestDTO clienteRequest) {
        try {
            return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    
                    Persona persona = clienteExistente.getPersona();
                    if (clienteRequest.getPersona().getNombre() != null) {
                        persona.setNombre(clienteRequest.getPersona().getNombre());
                    }
                    if (clienteRequest.getPersona().getGenero() != null) {
                        persona.setGenero(clienteRequest.getPersona().getGenero());
                    }
                    if (clienteRequest.getPersona().getEdad() != null) {
                        persona.setEdad(clienteRequest.getPersona().getEdad());
                    }
                    if (clienteRequest.getPersona().getDireccion() != null) {
                        persona.setDireccion(clienteRequest.getPersona().getDireccion());
                    }
                    if (clienteRequest.getPersona().getTelefono() != null) {
                        persona.setTelefono(clienteRequest.getPersona().getTelefono());
                    }
                    
                    Cliente clienteActualizado = clienteRepository.save(clienteExistente);
                    logger.info("Cliente ID: {} actualizado exitosamente", id);
                    
                    return clienteMapper.toResponseDTO(clienteActualizado);
                })
                .orElseThrow(() -> new ClienteNotFoundException(id));
        } catch (ClienteNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar cliente ID: {}", id, e);
            throw new RuntimeException("Error al actualizar el cliente");
        }
    }

    @Override
    public void desactivarCliente(Long id) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
            
            if ("INACTIVO".equals(cliente.getEstado())) {
                throw new ValidationException("El cliente ya se encuentra INACTIVO");
            }
            
            cliente.setEstado("INACTIVO");
            clienteRepository.save(cliente);
            logger.info("Cliente ID: {} desactivado exitosamente", id);
            
        } catch (ClienteNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al desactivar cliente ID: {}", id, e);
            throw new RuntimeException("Error al desactivar el cliente");
        }
    }

    @Override
    public boolean existeClientePorIdentificacion(String identificacion) {
        try {
            return clienteRepository.existsByPersonaIdentificacion(identificacion);
        } catch (Exception e) {
            logger.error("Error al verificar existencia de cliente con identificación: {}", identificacion, e);
            throw new RuntimeException("Error al verificar la existencia del cliente");
        }
    }
}
