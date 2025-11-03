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
 * DTO para transferencia de datos de productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de un producto con sus variantes")
public class ProductDto {

    @Schema(description = "ID único del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Nombre del producto", example = "Silla Caucana")
    private String name;

    @Schema(description = "Slug único para URLs", example = "silla-caucana")
    private String slug;

    @Schema(description = "Descripción detallada del producto")
    private String description;

    @Schema(description = "Array de URLs de imágenes del producto")
    private String[] images;

    @Schema(description = "Precio base del producto", example = "850000.00")
    private BigDecimal basePrice;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean isActive;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "ID de la categoría")
    private UUID categoryId;

    @Schema(description = "Información de la categoría")
    private String category;

    @Schema(description = "Slug de la categoría")
    private String categorySlug;

    @Schema(description = "Lista de variantes disponibles")
    private List<VariantDto> variants;

    @Schema(description = "Precio mínimo entre todas las variantes")
    private BigDecimal minPrice;

    @Schema(description = "Precio máximo entre todas las variantes")
    private BigDecimal maxPrice;

    @Schema(description = "Stock total disponible")
    private Integer totalStock;

    @Schema(description = "Indica si el producto tiene stock disponible")
    private Boolean hasStock;
}