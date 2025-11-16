package com.surequinos.surequinos_backend.application.dto.request;

import com.surequinos.surequinos_backend.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para crear un nuevo usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un nuevo usuario")
public class CreateUserRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String name;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Schema(description = "Correo electrónico", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Número de teléfono celular", example = "+57 300 1234567")
    private String phoneNumber;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Schema(
        description = "Rol del usuario. Valores permitidos: ADMIN (Administrador) o CLIENTE (Cliente)", 
        example = "CLIENTE", 
        allowableValues = {"ADMIN", "CLIENTE"}, 
        type = "string"
    )
    private UserRole role;

    @Schema(description = "Número de documento de identidad", example = "1234567890")
    private String documentNumber;
}

