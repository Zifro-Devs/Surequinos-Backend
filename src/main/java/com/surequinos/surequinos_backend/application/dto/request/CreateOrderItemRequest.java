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
    @Schema(description = "ID de la variante del producto", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID variantId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Schema(description = "Cantidad del producto", example = "2", required = true)
    private Integer quantity;
}

