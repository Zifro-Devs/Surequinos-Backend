package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surequinos.surequinos_backend.application.dto.VariantDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateVariantRequest;
import com.surequinos.surequinos_backend.application.dto.request.CreateVariantWithImageRequest;
import com.surequinos.surequinos_backend.application.service.ImageService;
import com.surequinos.surequinos_backend.application.service.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestión de variantes de productos
 */
@RestController
@RequestMapping("/variants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Variantes", description = "API para gestión de variantes de productos")
public class VariantController {

    private final VariantService variantService;
    private final ImageService imageService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Obtener variantes por producto", 
               description = "Retorna todas las variantes activas de un producto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variantes obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<VariantDto>> getVariantsByProductId(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId) {
        log.info("GET /variants/product/{} - Obteniendo variantes del producto", productId);
        
        List<VariantDto> variants = variantService.getVariantsByProductId(productId);
        
        log.info("Retornando {} variantes para el producto {}", variants.size(), productId);
        return ResponseEntity.ok(variants);
    }

    @Operation(summary = "Obtener variantes disponibles por producto", 
               description = "Retorna solo las variantes con stock disponible de un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variantes disponibles obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/product/{productId}/available")
    public ResponseEntity<List<VariantDto>> getAvailableVariantsByProductId(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId) {
        log.info("GET /variants/product/{}/available - Obteniendo variantes disponibles", productId);
        
        List<VariantDto> variants = variantService.getAvailableVariantsByProductId(productId);
        
        log.info("Retornando {} variantes disponibles para el producto {}", variants.size(), productId);
        return ResponseEntity.ok(variants);
    }

    @Operation(summary = "Obtener variante por SKU", 
               description = "Busca una variante específica por su SKU único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variante encontrada"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/sku/{sku}")
    public ResponseEntity<VariantDto> getVariantBySku(
            @Parameter(description = "SKU único de la variante", example = "SIL-CAU-ROBLE-14")
            @PathVariable String sku) {
        log.info("GET /variants/sku/{} - Buscando variante por SKU", sku);
        
        return variantService.getVariantBySku(sku)
            .map(variant -> {
                log.info("Variante encontrada: {}", variant.getSku());
                return ResponseEntity.ok(variant);
            })
            .orElseGet(() -> {
                log.warn("Variante no encontrada con SKU: {}", sku);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener variante por ID", 
               description = "Busca una variante específica por su ID único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variante encontrada"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VariantDto> getVariantById(
            @Parameter(description = "ID único de la variante")
            @PathVariable UUID id) {
        log.info("GET /variants/{} - Buscando variante por ID", id);
        
        return variantService.getVariantById(id)
            .map(variant -> {
                log.info("Variante encontrada: {}", variant.getSku());
                return ResponseEntity.ok(variant);
            })
            .orElseGet(() -> {
                log.warn("Variante no encontrada con ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Buscar variantes por atributos", 
               description = "Busca variantes de un producto filtradas por color y/o talla")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variantes encontradas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/product/{productId}/search")
    public ResponseEntity<List<VariantDto>> getVariantsByAttributes(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId,
            @Parameter(description = "Color a filtrar (opcional)", example = "Roble")
            @RequestParam(required = false) String color,
            @Parameter(description = "Talla a filtrar (opcional)", example = "14\"")
            @RequestParam(required = false) String size) {
        log.info("GET /variants/product/{}/search - Buscando variantes por atributos (color: {}, talla: {})", 
                productId, color, size);
        
        List<VariantDto> variants = variantService.getVariantsByAttributes(productId, color, size);
        
        log.info("Retornando {} variantes que coinciden con los filtros", variants.size());
        return ResponseEntity.ok(variants);
    }

    @Operation(summary = "Crear nueva variante", 
               description = "Crea una nueva variante para un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Variante creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe una variante con el mismo SKU"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<VariantDto> createVariant(
            @Parameter(description = "Datos de la nueva variante")
            @Valid @RequestBody CreateVariantRequest request) {
        log.info("POST /variants - Creando nueva variante: {}", request.getSku());
        
        try {
            VariantDto createdVariant = variantService.createVariant(request);
            
            log.info("Variante creada exitosamente: {} (ID: {})", 
                    createdVariant.getSku(), createdVariant.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVariant);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creando variante: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Crear variante con imagen", 
               description = "Crea una nueva variante con imagen que se sube a Cloudflare R2")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Variante creada exitosamente con imagen"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error en imagen"),
        @ApiResponse(responseCode = "409", description = "Ya existe una variante con el mismo SKU"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VariantDto> createVariantWithImage(
            @Parameter(description = "Datos de la variante en formato JSON")
            @RequestPart("variant") String variantJson,
            @Parameter(description = "Imagen de la variante")
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        log.info("POST /variants/with-image - Creando variante con imagen");
        log.info("Imagen recibida: {} (size: {} bytes, contentType: {})", 
                image != null ? image.getOriginalFilename() : "null",
                image != null ? image.getSize() : 0,
                image != null ? image.getContentType() : "null");
        
        try {
            // Parsear el JSON de la variante
            CreateVariantRequest variantRequest = objectMapper.readValue(variantJson, CreateVariantRequest.class);
            
            log.info("Creando variante: {} para producto: {}", variantRequest.getSku(), variantRequest.getProductId());
            
            // Subir imagen PRIMERO si se proporcionó
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                try {
                    log.info("Subiendo imagen para variante {} antes de crear la variante...", variantRequest.getSku());
                    imageUrl = imageService.uploadVariantImage(
                        image, variantRequest.getProductId(), variantRequest.getSku());
                    log.info("Imagen subida exitosamente: {}", imageUrl);
                    
                    // Setear la URL de la imagen en el request
                    variantRequest.setImageUrl(imageUrl);
                } catch (Exception e) {
                    log.error("Error subiendo imagen para variante {}: {}", 
                            variantRequest.getSku(), e.getMessage(), e);
                    throw new RuntimeException("Error subiendo imagen: " + e.getMessage(), e);
                }
            } else {
                log.warn("No se recibió imagen para la variante {}", variantRequest.getSku());
            }
            
            // Crear la variante con la URL de la imagen ya seteada
            VariantDto createdVariant = variantService.createVariant(variantRequest);
            
            log.info("Variante creada exitosamente con imagen: {} (ID: {}, imageUrl: {})", 
                    createdVariant.getSku(), createdVariant.getId(), createdVariant.getImageUrl());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVariant);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creando variante con imagen: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno creando variante con imagen: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar variante existente", 
               description = "Actualiza los datos de una variante existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variante actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "409", description = "Ya existe una variante con el mismo SKU"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VariantDto> updateVariant(
            @Parameter(description = "ID único de la variante")
            @PathVariable UUID id,
            @Parameter(description = "Nuevos datos de la variante")
            @Valid @RequestBody CreateVariantRequest request) {
        log.info("PUT /variants/{} - Actualizando variante", id);
        
        try {
            VariantDto updatedVariant = variantService.updateVariant(id, request);
            
            log.info("Variante actualizada exitosamente: {} (ID: {})", 
                    updatedVariant.getSku(), updatedVariant.getId());
            
            return ResponseEntity.ok(updatedVariant);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando variante {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar variante", 
               description = "Elimina una variante existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Variante eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariant(
            @Parameter(description = "ID único de la variante")
            @PathVariable UUID id) {
        log.info("DELETE /variants/{} - Eliminando variante", id);
        
        try {
            variantService.deleteVariant(id);
            
            log.info("Variante eliminada exitosamente: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Variante no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar stock de variante", 
               description = "Actualiza el stock disponible de una variante específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<VariantDto> updateStock(
            @Parameter(description = "ID único de la variante")
            @PathVariable UUID id,
            @Parameter(description = "Nuevo stock", example = "10")
            @RequestParam Integer stock) {
        log.info("PATCH /variants/{}/stock - Actualizando stock a {}", id, stock);
        
        try {
            VariantDto updatedVariant = variantService.updateStock(id, stock);
            
            log.info("Stock actualizado exitosamente para variante: {} (Nuevo stock: {})", 
                    updatedVariant.getSku(), stock);
            
            return ResponseEntity.ok(updatedVariant);
            
        } catch (IllegalArgumentException e) {
            log.warn("Variante no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Reducir stock de variante", 
               description = "Reduce el stock de una variante (para procesar ventas)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock reducido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/reduce-stock")
    public ResponseEntity<String> reduceStock(
            @Parameter(description = "ID único de la variante")
            @PathVariable UUID id,
            @Parameter(description = "Cantidad a reducir", example = "2")
            @RequestParam Integer quantity) {
        log.info("PATCH /variants/{}/reduce-stock - Reduciendo stock en {}", id, quantity);
        
        boolean success = variantService.reduceStock(id, quantity);
        
        if (success) {
            log.info("Stock reducido exitosamente para variante: {} (Cantidad: {})", id, quantity);
            return ResponseEntity.ok("Stock reducido exitosamente");
        } else {
            log.warn("No se pudo reducir el stock para variante: {} (stock insuficiente)", id);
            return ResponseEntity.badRequest().body("Stock insuficiente");
        }
    }

    @Operation(summary = "Obtener colores disponibles", 
               description = "Retorna los colores disponibles (con stock) para un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Colores obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/product/{productId}/colors")
    public ResponseEntity<List<String>> getAvailableColors(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId) {
        log.info("GET /variants/product/{}/colors - Obteniendo colores disponibles", productId);
        
        List<String> colors = variantService.getAvailableColorsByProductId(productId);
        
        log.info("Retornando {} colores disponibles para el producto {}", colors.size(), productId);
        return ResponseEntity.ok(colors);
    }

    @Operation(summary = "Obtener tallas disponibles", 
               description = "Retorna las tallas disponibles para un producto y color específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tallas obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/product/{productId}/sizes")
    public ResponseEntity<List<String>> getAvailableSizes(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId,
            @Parameter(description = "Color a filtrar (opcional)", example = "Roble")
            @RequestParam(required = false) String color) {
        log.info("GET /variants/product/{}/sizes - Obteniendo tallas disponibles (color: {})", productId, color);
        
        List<String> sizes = variantService.getAvailableSizesByProductIdAndColor(productId, color);
        
        log.info("Retornando {} tallas disponibles para el producto {} y color {}", 
                sizes.size(), productId, color);
        return ResponseEntity.ok(sizes);
    }

    @Operation(summary = "Obtener variantes con stock bajo", 
               description = "Retorna variantes que tienen stock por debajo del umbral especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variantes con stock bajo obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<Object[]>> getVariantsWithLowStock(
            @Parameter(description = "Umbral de stock bajo", example = "5")
            @RequestParam(defaultValue = "5") Integer threshold) {
        log.info("GET /variants/low-stock?threshold={} - Obteniendo variantes con stock bajo", threshold);
        
        List<Object[]> variants = variantService.getVariantsWithLowStock(threshold);
        
        log.info("Retornando {} variantes con stock bajo (umbral: {})", variants.size(), threshold);
        return ResponseEntity.ok(variants);
    }


}