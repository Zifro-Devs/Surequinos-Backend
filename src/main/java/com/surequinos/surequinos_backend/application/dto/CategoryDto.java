package com.surequinos.surequinos_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para transferencia de datos de categorías
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una categoría de productos")
public class CategoryDto {

    @Schema(description = "ID único de la categoría", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID de la categoría padre (si es subcategoría)", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID parentId;

    @Schema(description = "Nombre de la categoría", example = "Sillas de Montar")
    private String name;

    @Schema(description = "Slug único para URLs", example = "sillas-de-montar")
    private String slug;

    @Schema(description = "Orden de visualización", example = "1")
    private Integer displayOrder;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Subcategorías (si las tiene)")
    private List<CategoryDto> subcategories;

    @Schema(description = "Cantidad de productos en esta categoría")
    private Long productCount;
}