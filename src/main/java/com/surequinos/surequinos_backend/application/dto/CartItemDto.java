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
 * DTO para transferencia de datos de items del carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item del carrito de compras")
public class CartItemDto {

    @Schema(description = "ID único del item en el carrito", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID de la variante del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID variantId;

    @Schema(description = "Información de la variante")
    private VariantDto variant;

    @Schema(description = "Información del producto")
    private ProductDto product;

    @Schema(description = "Cantidad del item", example = "2")
    private Integer quantity;

    @Schema(description = "Precio unitario al momento de agregar", example = "850000.00")
    private BigDecimal price;

    @Schema(description = "Subtotal del item (precio * cantidad)", example = "1700000.00")
    private BigDecimal subtotal;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
