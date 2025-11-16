package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.AddressDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateAddressRequest;
import com.surequinos.surequinos_backend.application.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para gestión de direcciones
 */
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Addresses", description = "API para gestión de direcciones de usuarios")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Obtener direcciones por email de usuario", 
               description = "Retorna todas las direcciones asociadas a un usuario buscándolo por su correo electrónico. Las direcciones se ordenan con la dirección por defecto primero, luego por fecha de creación descendente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Direcciones obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/user/{email:.+}")
    public ResponseEntity<List<AddressDto>> getAddressesByUserEmail(
            @Parameter(description = "Correo electrónico del usuario", example = "cliente@example.com")
            @PathVariable String email) {
        log.info("GET /addresses/user/{} - Obteniendo direcciones por email", email);
        
        List<AddressDto> addresses = addressService.getAddressesByUserEmail(email);
        
        log.info("Retornando {} direcciones para usuario con email: {}", addresses.size(), email);
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "Obtener direcciones por ID de usuario", 
               description = "Retorna todas las direcciones asociadas a un usuario por su ID. Las direcciones se ordenan con la dirección por defecto primero, luego por fecha de creación descendente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Direcciones obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/user/id/{userId}")
    public ResponseEntity<List<AddressDto>> getAddressesByUserId(
            @Parameter(description = "ID único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId) {
        log.info("GET /addresses/user/id/{} - Obteniendo direcciones por ID de usuario", userId);
        
        List<AddressDto> addresses = addressService.getAddressesByUserId(userId);
        
        log.info("Retornando {} direcciones para usuario con ID: {}", addresses.size(), userId);
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "Crear nueva dirección", 
               description = "Crea una nueva dirección para un usuario. Si se marca como por defecto, se desmarca automáticamente la dirección por defecto anterior. Si es la primera dirección del usuario, se marca automáticamente como por defecto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dirección creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<AddressDto> createAddress(
            @Parameter(description = "ID único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId,
            @RequestBody CreateAddressRequest request) {
        log.info("POST /addresses/user/{} - Creando nueva dirección", userId);
        
        AddressDto address = addressService.createAddress(userId, request);
        
        log.info("Dirección creada exitosamente: {} (ID: {})", address.getStreet(), address.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }
}

