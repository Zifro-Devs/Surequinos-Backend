package com.surequinos.surequinos_backend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO para crear una nueva orden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva orden")
public class CreateOrderRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario/cliente", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID userId;

    @PositiveOrZero(message = "El valor del descuento no puede ser negativo")
    @Schema(description = "Valor del descuento aplicado", example = "50000.00")
    @Builder.Default
    private BigDecimal discountValue = BigDecimal.ZERO;

    @Schema(description = "Notas adicionales de la orden")
    private String notes;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Schema(description = "Dirección de envío", example = "Calle 123 #45-67, Barrio Centro", required = true)
    private String shippingAddress;

    @PositiveOrZero(message = "El valor del envío no puede ser negativo")
    @Schema(description = "Valor del envío", example = "15000.00")
    @Builder.Default
    private BigDecimal shippingValue = BigDecimal.ZERO;

    @NotEmpty(message = "La orden debe tener al menos un item")
    @Schema(description = "Items de la orden", required = true)
    @Valid
    private List<CreateOrderItemRequest> items;
}

