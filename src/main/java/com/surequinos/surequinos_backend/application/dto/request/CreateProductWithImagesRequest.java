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
 * Request DTO para crear un producto con manejo de imágenes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un nuevo producto con imágenes")
public class CreateProductWithImagesRequest {

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "ID de la categoría", required = true)
    private UUID categoryId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    @Schema(description = "Nombre del producto", required = true)
    private String name;

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 200, message = "El slug no puede exceder 200 caracteres")
    @Schema(description = "Slug único para URLs", required = true)
    private String slug;

    @Schema(description = "Descripción detallada del producto")
    private String description;

    @Positive(message = "El precio base debe ser positivo")
    @Schema(description = "Precio base del producto")
    private BigDecimal basePrice;

    @Schema(description = "Indica si el producto está activo", example = "true")
    @Builder.Default
    private Boolean isActive = true;

    // Las imágenes se manejan por separado en el controlador con MultipartFile[]
}