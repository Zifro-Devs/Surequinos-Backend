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
 * DTO para transferencia de datos de órdenes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Orden de compra")
public class OrderDto {

    @Schema(description = "ID único de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Número de orden (único, diferente al ID)", example = "ORD-2024-001")
    private String orderNumber;

    @Schema(description = "ID del usuario/cliente", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Valor del descuento aplicado", example = "50000.00")
    private BigDecimal discountValue;

    @Schema(description = "Notas adicionales de la orden")
    private String notes;

    @Schema(description = "Estado del pago", example = "PAID", allowableValues = {"PENDING", "PAID", "FAILED", "REFUNDED"})
    private String paymentStatus;

    @Schema(description = "Valor del envío", example = "15000.00")
    private BigDecimal shippingValue;

    @Schema(description = "Estado de la orden", example = "CONFIRMED", allowableValues = {"PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status;

    @Schema(description = "Subtotal (valor sin descuentos ni envío)", example = "1700000.00")
    private BigDecimal subtotal;

    @Schema(description = "Total (con descuentos y envío aplicados)", example = "1665000.00")
    private BigDecimal total;

    @Schema(description = "Dirección de envío", example = "Calle 123 #45-67, Barrio Centro")
    private String shippingAddress;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;

    // Información adicional (opcional, se puede enriquecer)
    @Schema(description = "Nombre del usuario/cliente")
    private String userName;

    @Schema(description = "Email del usuario/cliente")
    private String userEmail;

    @Schema(description = "Items de la orden")
    private List<OrderItemDto> orderItems;
}

