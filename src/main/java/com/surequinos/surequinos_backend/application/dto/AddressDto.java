package com.surequinos.surequinos_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para representar una dirección
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una dirección de envío")
public class AddressDto {

    @Schema(description = "ID único de la dirección", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID del usuario propietario de la dirección", example = "223e4567-e89b-12d3-a456-426614174001")
    private UUID userId;

    @Schema(description = "Calle y número", example = "Calle 123 #45-67")
    private String street;

    @Schema(description = "Ciudad", example = "Bogotá")
    private String city;

    @Schema(description = "Estado/Departamento", example = "Cundinamarca")
    private String state;

    @Schema(description = "País", example = "Colombia")
    private String country;

    @Schema(description = "Información adicional (referencias, apartamento, etc.)", example = "Apartamento 301, Edificio Los Rosales")
    private String additionalInfo;

    @Schema(description = "Indica si es la dirección por defecto", example = "true")
    private Boolean isDefault;

    @Schema(description = "Fecha de creación", example = "2024-11-16T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2024-11-16T10:30:00")
    private LocalDateTime updatedAt;
}

