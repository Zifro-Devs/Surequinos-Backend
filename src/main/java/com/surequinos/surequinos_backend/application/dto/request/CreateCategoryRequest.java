package com.surequinos.surequinos_backend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO para crear una nueva categoría
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva categoría")
public class CreateCategoryRequest {

    @Schema(description = "ID de la categoría padre (opcional para subcategorías)")
    private UUID parentId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Nombre de la categoría", example = "Sillas de Montar", required = true)
    private String name;

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 100, message = "El slug no puede exceder 100 caracteres")
    @Schema(description = "Slug único para URLs", example = "sillas-de-montar", required = true)
    private String slug;

    @Schema(description = "Orden de visualización", example = "1")
    private Integer displayOrder;
}