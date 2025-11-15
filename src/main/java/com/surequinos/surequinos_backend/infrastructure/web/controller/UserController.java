package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.UserDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateUserRequest;
import com.surequinos.surequinos_backend.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestión de usuarios
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "API para gestión de usuarios/clientes")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Obtener todos los usuarios", 
               description = "Retorna una lista de todos los usuarios registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET /users - Obteniendo todos los usuarios");
        
        List<UserDto> users = userService.getAllUsers();
        
        log.info("Retornando {} usuarios", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID", 
               description = "Busca un usuario específico por su ID único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID único del usuario")
            @PathVariable UUID id) {
        log.info("GET /users/{} - Buscando usuario por ID", id);
        
        return userService.getUserById(id)
            .map(user -> {
                log.info("Usuario encontrado: {}", user.getEmail());
                return ResponseEntity.ok(user);
            })
            .orElseGet(() -> {
                log.warn("Usuario no encontrado con ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener usuario por email", 
               description = "Busca un usuario específico por su email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(
            @Parameter(description = "Email del usuario")
            @PathVariable String email) {
        log.info("GET /users/email/{} - Buscando usuario por email", email);
        
        return userService.getUserByEmail(email)
            .map(user -> {
                log.info("Usuario encontrado: {}", user.getEmail());
                return ResponseEntity.ok(user);
            })
            .orElseGet(() -> {
                log.warn("Usuario no encontrado con email: {}", email);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Crear nuevo usuario", 
               description = "Crea un nuevo usuario/cliente en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un usuario con el mismo email o documento"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "Datos del nuevo usuario")
            @Valid @RequestBody CreateUserRequest request) {
        log.info("POST /users - Creando nuevo usuario: {}", request.getEmail());
        
        try {
            UserDto createdUser = userService.createUser(request);
            
            log.info("Usuario creado exitosamente: {} (ID: {})", 
                    createdUser.getEmail(), createdUser.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creando usuario: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar usuario existente", 
               description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "409", description = "Ya existe un usuario con el mismo email o documento"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID único del usuario")
            @PathVariable UUID id,
            @Parameter(description = "Nuevos datos del usuario")
            @Valid @RequestBody CreateUserRequest request) {
        log.info("PUT /users/{} - Actualizando usuario", id);
        
        try {
            UserDto updatedUser = userService.updateUser(id, request);
            
            log.info("Usuario actualizado exitosamente: {} (ID: {})", 
                    updatedUser.getEmail(), updatedUser.getId());
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando usuario {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar usuario", 
               description = "Elimina un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID único del usuario")
            @PathVariable UUID id) {
        log.info("DELETE /users/{} - Eliminando usuario", id);
        
        try {
            userService.deleteUser(id);
            
            log.info("Usuario eliminado exitosamente: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Usuario no encontrado: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}

