package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.UserDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateUserRequest;
import com.surequinos.surequinos_backend.application.service.UserService;
import com.surequinos.surequinos_backend.domain.enums.UserRole;
import com.surequinos.surequinos_backend.domain.enums.UserStatus;
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

    @Operation(
        summary = "Crear nuevo usuario", 
        description = """
            Crea un nuevo usuario/cliente en el sistema.
            
            **Rol del usuario:**
            - `ADMIN`: Administrador del sistema
            - `CLIENTE`: Cliente que puede realizar compras
            
            El rol se especifica usando el enum UserRole (ADMIN o CLIENTE).
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un usuario con el mismo email o documento"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Parameter(
                description = "Datos del nuevo usuario. El campo 'role' debe ser 'ADMIN' o 'CLIENTE'",
                required = true
            )
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

    @Operation(summary = "Búsqueda unificada de usuarios", 
               description = """
                   Endpoint unificado para buscar usuarios con todos los filtros posibles. 
                   Todos los parámetros son opcionales y se combinan con AND (todos deben cumplirse).
                   
                   **Filtros disponibles:**
                   - **Texto**: name, email, documentNumber, phoneNumber (búsqueda parcial, case-insensitive)
                   - **Roles**: roles (múltiples roles separados por comas - filtro OR)
                   - **Estados**: statuses (múltiples estados separados por comas - filtro OR)
                   - **Fechas**: startDate, endDate (rango de fechas de creación)
                   
                   **Ejemplos de uso:**
                   - Solo clientes activos: `?roles=CLIENTE&statuses=ACTIVE`
                   - Clientes e inactivos: `?roles=CLIENTE&statuses=ACTIVE,INACTIVE`
                   - Administradores creados en noviembre: `?roles=ADMIN&startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59`
                   - Buscar por nombre y rol: `?name=Juan&roles=CLIENTE`
                   """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios encontrados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos (fechas mal formateadas, etc.)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(
            @Parameter(description = "Nombre del usuario (búsqueda parcial, case-insensitive)", example = "Juan")
            @RequestParam(required = false) String name,
            @Parameter(description = "Email del usuario (búsqueda parcial, case-insensitive)", example = "juan@example.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Número de documento del usuario (búsqueda parcial)", example = "1234567890")
            @RequestParam(required = false) String documentNumber,
            @Parameter(description = "Número de teléfono del usuario (búsqueda parcial)", example = "3001234567")
            @RequestParam(required = false) String phoneNumber,
            @Parameter(description = "Rol(es) del usuario. Múltiples valores separados por comas. Valores: ADMIN, CLIENTE", example = "CLIENTE")
            @RequestParam(required = false) List<UserRole> roles,
            @Parameter(description = "Estado(s) del usuario. Múltiples valores separados por comas. Valores: ACTIVE, INACTIVE, DELETED", example = "ACTIVE")
            @RequestParam(required = false) List<UserStatus> statuses,
            @Parameter(description = "Fecha de inicio del rango (ISO 8601)", example = "2024-11-01T00:00:00")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Fecha de fin del rango (ISO 8601)", example = "2024-11-30T23:59:59")
            @RequestParam(required = false) String endDate) {
        log.info("GET /users/search - Buscando usuarios con criterios: name={}, email={}, documentNumber={}, phoneNumber={}, roles={}, statuses={}, startDate={}, endDate={}", 
                name, email, documentNumber, phoneNumber, roles, statuses, startDate, endDate);
        
        // Parsear fechas si se proporcionan
        java.time.LocalDateTime start = null;
        java.time.LocalDateTime end = null;
        
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = java.time.LocalDateTime.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = java.time.LocalDateTime.parse(endDate);
            }
            
            // Validar que la fecha de inicio sea anterior a la fecha de fin
            if (start != null && end != null && start.isAfter(end)) {
                log.warn("Fecha de inicio es posterior a la fecha de fin");
                return ResponseEntity.badRequest().build();
            }
        } catch (java.time.format.DateTimeParseException e) {
            log.warn("Error parseando fechas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        
        List<UserDto> users = userService.searchUsers(name, email, documentNumber, phoneNumber, roles, statuses, start, end);
        
        log.info("Retornando {} usuarios encontrados", users.size());
        return ResponseEntity.ok(users);
    }
}

