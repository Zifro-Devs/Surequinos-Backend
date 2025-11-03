package com.surequinos.surequinos_backend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para filtros disponibles en productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filtros disponibles para productos")
public class ProductFiltersDto {

    @Schema(description = "Colores disponibles")
    private List<String> colors;

    @Schema(description = "Tallas disponibles")
    private List<String> sizes;

    @Schema(description = "Tipos disponibles")
    private List<String> types;

    @Schema(description = "Precio mínimo disponible")
    private BigDecimal minPrice;

    @Schema(description = "Precio máximo disponible")
    private BigDecimal maxPrice;

    @Schema(description = "Categorías disponibles")
    private List<CategoryFilterDto> categories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información de categoría para filtros")
    public static class CategoryFilterDto {
        @Schema(description = "Nombre de la categoría")
        private String name;
        
        @Schema(description = "Slug de la categoría")
        private String slug;
        
        @Schema(description = "Número de productos en la categoría")
        private Long productCount;
    }
}