package com.surequinos.surequinos_backend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO para operaciones de subida de imágenes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de subida de imágenes")
public class ImageUploadResponse {

    @Schema(description = "Indica si la operación fue exitosa")
    private Boolean success;

    @Schema(description = "Mensaje descriptivo")
    private String message;

    @Schema(description = "URL de la imagen subida (para una sola imagen)")
    private String imageUrl;

    @Schema(description = "URLs de las imágenes subidas (para múltiples imágenes)")
    private List<String> imageUrls;

    @Schema(description = "Timestamp de la operación")
    private LocalDateTime timestamp;

    @Schema(description = "Detalles adicionales o errores")
    private String details;

    public static ImageUploadResponse success(String imageUrl) {
        return ImageUploadResponse.builder()
            .success(true)
            .message("Imagen subida exitosamente")
            .imageUrl(imageUrl)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static ImageUploadResponse success(List<String> imageUrls) {
        return ImageUploadResponse.builder()
            .success(true)
            .message("Imágenes subidas exitosamente")
            .imageUrls(imageUrls)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static ImageUploadResponse error(String message, String details) {
        return ImageUploadResponse.builder()
            .success(false)
            .message(message)
            .details(details)
            .timestamp(LocalDateTime.now())
            .build();
    }
}