package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.request.ApplyDiscountRequest;
import com.surequinos.surequinos_backend.application.service.PromotionService;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promotions", description = "API para gestión de promociones y descuentos")
public class PromotionController {

    private final PromotionService promotionService;

    @Operation(summary = "Aplicar descuento a una variante",
            description = "Aplica un descuento porcentual a una variante específica. Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Descuento aplicado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Variante no encontrada")
    })
    @PostMapping("/apply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Variant> applyDiscount(@Valid @RequestBody ApplyDiscountRequest request) {
        log.info("POST /promotions/apply - Aplicando descuento a variante: {}", request.getVariantId());

        Variant variant = promotionService.applyDiscount(request);

        return ResponseEntity.ok(variant);
    }

    @Operation(summary = "Actualizar descuento de una variante",
            description = "Actualiza el descuento de una variante específica. Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Descuento actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Variante no encontrada")
    })
    @PutMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Variant> updateDiscount(
            @Parameter(description = "ID de la variante")
            @PathVariable UUID variantId,
            @Valid @RequestBody ApplyDiscountRequest request) {
        log.info("PUT /promotions/{} - Actualizando descuento", variantId);

        // Asegurar que el ID de la URL coincida con el del body
        request.setVariantId(variantId);
        Variant variant = promotionService.applyDiscount(request);

        return ResponseEntity.ok(variant);
    }

    @Operation(summary = "Remover descuento de una variante",
            description = "Elimina el descuento de una variante específica. Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Descuento removido exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Variante no encontrada")
    })
    @DeleteMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Variant> removeDiscount(
            @Parameter(description = "ID de la variante")
            @PathVariable UUID variantId) {
        log.info("DELETE /promotions/{} - Removiendo descuento", variantId);

        Variant variant = promotionService.removeDiscount(variantId);

        return ResponseEntity.ok(variant);
    }

    @Operation(summary = "Obtener variantes con descuentos activos",
            description = "Retorna todas las variantes que tienen descuentos activos en este momento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @GetMapping("/active")
    public ResponseEntity<List<Variant>> getActiveDiscounts() {
        log.info("GET /promotions/active - Obteniendo variantes con descuentos activos");

        List<Variant> variants = promotionService.getVariantsWithActiveDiscounts();

        return ResponseEntity.ok(variants);
    }

    @Operation(summary = "Obtener todas las variantes con descuentos",
            description = "Retorna todas las variantes que tienen descuentos configurados (activos o programados). Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Variant>> getAllDiscounts() {
        log.info("GET /promotions/all - Obteniendo todas las variantes con descuentos");

        List<Variant> variants = promotionService.getVariantsWithDiscounts();

        return ResponseEntity.ok(variants);
    }
}
