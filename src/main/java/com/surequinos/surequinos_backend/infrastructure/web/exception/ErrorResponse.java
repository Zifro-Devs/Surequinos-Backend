package com.surequinos.surequinos_backend.infrastructure.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Respuesta estándar para errores de la API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta estándar para errores de la API")
public class ErrorResponse {

    @Schema(description = "Timestamp del error", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP", example = "400")
    private Integer status;

    @Schema(description = "Tipo de error", example = "Bad Request")
    private String error;

    @Schema(description = "Mensaje descriptivo del error", example = "Los datos enviados no son válidos")
    private String message;

    @Schema(description = "Detalles adicionales del error (opcional)")
    private Map<String, String> details;

    @Schema(description = "Ruta donde ocurrió el error (opcional)", example = "/api/products")
    private String path;
}