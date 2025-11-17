package com.surequinos.surequinos_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para transferencia de datos del carrito de compras
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Carrito de compras")
public class CartDto {

    @Schema(description = "ID único del carrito", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID del usuario (null si es anónimo)")
    private UUID userId;

    @Schema(description = "ID de sesión para usuarios anónimos")
    private String sessionId;

    @Schema(description = "Items en el carrito")
    private List<CartItemDto> items;

    @Schema(description = "Subtotal del carrito", example = "1500000.00")
    private BigDecimal subtotal;

    @Schema(description = "Cantidad total de items", example = "3")
    private Integer itemCount;

    @Schema(description = "Fecha de expiración del carrito")
    private LocalDateTime expiresAt;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
