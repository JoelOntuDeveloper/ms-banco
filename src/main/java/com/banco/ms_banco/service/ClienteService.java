package com.banco.ms_banco.service;

import java.util.List;
import java.util.Optional;

import com.banco.ms_banco.dto.clientes.ClienteRequestDTO;
import com.banco.ms_banco.dto.clientes.ClienteResponseDTO;
import com.banco.ms_banco.dto.clientes.ClienteUpdateRequestDTO;

public interface ClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO clienteRequest);
    List<ClienteResponseDTO> obtenerTodosLosClientes();
    Optional<ClienteResponseDTO> obtenerClientePorId(Long id);
    Optional<ClienteResponseDTO> obtenerClientePorIdentificacion(String identificacion);
    ClienteResponseDTO actualizarCliente(Long id, ClienteUpdateRequestDTO clienteRequest);
    void desactivarCliente(Long id);
    boolean existeClientePorIdentificacion(String identificacion);

}
