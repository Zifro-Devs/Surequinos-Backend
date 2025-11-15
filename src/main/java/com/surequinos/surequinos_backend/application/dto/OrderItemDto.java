package com.surequinos.surequinos_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para transferencia de datos de items de orden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item de una orden")
public class OrderItemDto {

    @Schema(description = "ID único del item", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID orderId;

    @Schema(description = "ID de la variante del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID variantId;

    @Schema(description = "Cantidad del producto", example = "2")
    private Integer quantity;

    @Schema(description = "Precio unitario", example = "850000.00")
    private BigDecimal unitPrice;

    @Schema(description = "Precio total (cantidad * precio unitario)", example = "1700000.00")
    private BigDecimal totalPrice;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    // Información adicional de la variante (opcional, se puede enriquecer)
    @Schema(description = "SKU de la variante")
    private String variantSku;

    @Schema(description = "Nombre del producto")
    private String productName;
}

