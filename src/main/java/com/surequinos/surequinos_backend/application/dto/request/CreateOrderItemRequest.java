package com.surequinos.surequinos_backend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO para crear un item de orden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un item de orden")
public class CreateOrderItemRequest {

    @NotNull(message = "El ID de la variante es obligatorio")
    @Schema(description = "ID de la variante del producto", example = "750e8400-e29b-41d4-a716-446655440001", required = true)
    private UUID variantId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Schema(description = "Cantidad del producto", example = "2", required = true)
    private Integer quantity;
}

