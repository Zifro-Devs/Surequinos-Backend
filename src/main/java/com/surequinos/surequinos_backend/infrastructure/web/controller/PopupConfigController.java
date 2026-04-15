package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.PopupConfigDto;
import com.surequinos.surequinos_backend.application.service.PopupConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/popup-config")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Popup Configuration", description = "API para gestión de configuración del popup de bienvenida")
public class PopupConfigController {

    private final PopupConfigService popupConfigService;

    @Operation(summary = "Obtener configuración activa del popup")
    @GetMapping("/active")
    public ResponseEntity<PopupConfigDto> getActiveConfig() {
        log.info("GET /popup-config/active - Obteniendo configuración activa");
        PopupConfigDto config = popupConfigService.getActiveConfig();
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Actualizar configuración del popup")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PopupConfigDto> updateConfig(@RequestBody @jakarta.validation.Valid PopupConfigDto dto) {
        log.info("PUT /popup-config - Actualizando configuración: {}", dto);
        try {
            PopupConfigDto updated = popupConfigService.updateConfig(dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error actualizando configuración del popup", e);
            throw e;
        }
    }
}
