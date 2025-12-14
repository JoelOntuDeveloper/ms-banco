package com.banco.ms_banco.dto.movimientos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovimientoRequestDTO {
    @NotNull(message = "El valor es obligatorio")
    @Digits(integer = 15, fraction = 2, message = "El valor debe tener máximo 15 enteros y 2 decimales")
    private BigDecimal valor;
}