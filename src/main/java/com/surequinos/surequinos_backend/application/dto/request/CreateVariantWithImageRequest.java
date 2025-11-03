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
 * Request DTO para crear una variante con imagen específica
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva variante con imagen")
public class CreateVariantWithImageRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto", required = true)
    private UUID productId;

    @NotBlank(message = "El SKU es obligatorio")
    @Schema(description = "SKU único de la variante", required = true)
    private String sku;

    @Schema(description = "Color de la variante")
    private String color;

    @Schema(description = "Talla de la variante")
    private String size;

    @Schema(description = "Tipo de la variante")
    private String type;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    @Schema(description = "Precio de la variante", required = true)
    private BigDecimal price;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Schema(description = "Stock disponible")
    @Builder.Default
    private Integer stock = 0;

    @Schema(description = "Indica si la variante está activa")
    @Builder.Default
    private Boolean isActive = true;

    // La imagen se maneja por separado en el controlador con MultipartFile
}