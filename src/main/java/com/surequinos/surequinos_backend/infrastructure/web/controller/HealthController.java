package com.surequinos.surequinos_backend.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador de salud para verificar que la API está funcionando
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Endpoints de salud de la aplicación")
public class HealthController {

    @Operation(summary = "Verificar estado de la aplicación")
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "Surequinos Backend API",
            "version", "1.0.0"
        ));
    }

    @Operation(summary = "Verificar conectividad básica")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}