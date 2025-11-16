package com.surequinos.surequinos_backend.application.dto;

import com.surequinos.surequinos_backend.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para transferencia de datos de usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Usuario/cliente del sistema")
public class UserDto {

    @Schema(description = "ID único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String name;

    @Schema(description = "Correo electrónico", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Número de teléfono celular", example = "+57 300 1234567")
    private String phoneNumber;

    @Schema(description = "ID del rol del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID roleId;

    @Schema(
        description = "Rol del usuario. Valores: ADMIN (Administrador) o CLIENTE (Cliente)", 
        example = "CLIENTE", 
        allowableValues = {"ADMIN", "CLIENTE"},
        type = "string"
    )
    private UserRole role;

    @Schema(description = "Número de documento de identidad", example = "1234567890")
    private String documentNumber;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}

