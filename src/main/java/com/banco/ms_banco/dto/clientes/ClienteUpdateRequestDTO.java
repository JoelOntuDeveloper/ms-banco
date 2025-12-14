package com.banco.ms_banco.dto.clientes;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteUpdateRequestDTO {
    @NotNull(message = "Los datos de la persona son obligatorios")
    private PersonaDTO persona;
}