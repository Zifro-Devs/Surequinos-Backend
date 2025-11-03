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
 * DTO para transferencia de datos de variantes de productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Variante específica de un producto")
public class VariantDto {

    @Schema(description = "ID único de la variante", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "SKU único de la variante", example = "SIL-CAU-ROBLE-14")
    private String sku;

    @Schema(description = "Color de la variante", example = "Roble")
    private String color;

    @Schema(description = "Talla de la variante", example = "14\"")
    private String size;

    @Schema(description = "Tipo de la variante", example = "Americana")
    private String type;

    @Schema(description = "Precio de la variante", example = "850000.00")
    private BigDecimal price;

    @Schema(description = "Stock disponible", example = "5")
    private Integer stock;

    @Schema(description = "URL de imagen específica de la variante")
    private String imageUrl;

    @Schema(description = "Indica si la variante está activa", example = "true")
    private Boolean isActive;

    @Schema(description = "Indica si la variante está disponible (stock > 0 y activa)", example = "true")
    private Boolean available;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;
}