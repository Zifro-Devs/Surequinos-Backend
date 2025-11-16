package com.surequinos.surequinos_backend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para crear una nueva dirección
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva dirección")
public class CreateAddressRequest {

    @NotBlank(message = "La calle es obligatoria")
    @Schema(description = "Calle y número", example = "Calle 123 #45-67", required = true)
    private String street;

    @NotBlank(message = "La ciudad es obligatoria")
    @Schema(description = "Ciudad", example = "Bogotá", required = true)
    private String city;

    @Schema(description = "Estado/Departamento", example = "Cundinamarca")
    private String state;

    @Schema(description = "País", example = "Colombia")
    private String country;

    @Schema(description = "Información adicional (referencias, apartamento, etc.)", example = "Apartamento 301, Edificio Los Rosales")
    private String additionalInfo;

    @Schema(description = "Indica si es la dirección por defecto", example = "false")
    @Builder.Default
    private Boolean isDefault = false;
}

