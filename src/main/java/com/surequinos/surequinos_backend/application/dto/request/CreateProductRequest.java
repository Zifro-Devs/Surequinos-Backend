package com.surequinos.surequinos_backend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO para crear un nuevo producto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un nuevo producto")
public class CreateProductRequest {

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "ID de la categoría", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID categoryId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    @Schema(description = "Nombre del producto", example = "Silla Caucana", required = true)
    private String name;

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 200, message = "El slug no puede exceder 200 caracteres")
    @Schema(description = "Slug único para URLs", example = "silla-caucana", required = true)
    private String slug;

    @Schema(description = "Descripción detallada del producto")
    private String description;

    @Schema(description = "Array de URLs de imágenes del producto")
    private String[] images;

    @Positive(message = "El precio base debe ser positivo")
    @Schema(description = "Precio base del producto", example = "850000.00")
    private BigDecimal basePrice;

    @Schema(description = "Indica si el producto está activo", example = "true")
    @Builder.Default
    private Boolean isActive = true;
}