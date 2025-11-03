package com.surequinos.surequinos_backend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO resumido para listados de productos (optimizado para performance)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información resumida de un producto para listados")
public class ProductSummaryDto {

    @Schema(description = "ID único del producto")
    private UUID id;

    @Schema(description = "Nombre del producto")
    private String name;

    @Schema(description = "Slug único para URLs")
    private String slug;

    @Schema(description = "Descripción corta del producto")
    private String shortDescription;

    @Schema(description = "URL de la imagen principal")
    private String mainImage;

    @Schema(description = "Precio mínimo entre todas las variantes")
    private BigDecimal minPrice;

    @Schema(description = "Precio máximo entre todas las variantes")
    private BigDecimal maxPrice;

    @Schema(description = "Nombre de la categoría")
    private String category;

    @Schema(description = "Slug de la categoría")
    private String categorySlug;

    @Schema(description = "Indica si tiene stock disponible")
    private Boolean hasStock;

    @Schema(description = "Número total de variantes")
    private Integer variantCount;

    @Schema(description = "Etiqueta especial (Nuevo, Popular, Oferta, etc.)")
    private String badge;

    @Schema(description = "Porcentaje de descuento si aplica")
    private Integer discountPercentage;
}