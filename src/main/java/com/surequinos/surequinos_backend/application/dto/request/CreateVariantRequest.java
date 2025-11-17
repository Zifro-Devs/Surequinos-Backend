package com.surequinos.surequinos_backend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO para crear una nueva variante de producto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva variante de producto")
public class CreateVariantRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID productId;

    @NotBlank(message = "El SKU es obligatorio")
    @Schema(description = "SKU único de la variante", example = "SIL-CAU-ROBLE-14", required = true)
    private String sku;

    @Schema(description = "Color de la variante", example = "Roble")
    private String color;

    @Schema(description = "Talla de la variante", example = "14\"")
    private String size;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    @Schema(description = "Precio de la variante", example = "850000.00", required = true)
    private BigDecimal price;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Schema(description = "Stock disponible", example = "5")
    @Builder.Default
    private Integer stock = 0;

    @Schema(description = "URL de imagen específica de la variante")
    private String imageUrl;

    @Schema(description = "Indica si la variante está activa", example = "true")
    @Builder.Default
    private Boolean isActive = true;
}