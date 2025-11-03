package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.response.ImageUploadResponse;
import com.surequinos.surequinos_backend.application.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para gestión de imágenes en Cloudflare R2
 */
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Imágenes", description = "API para gestión de imágenes en Cloudflare R2")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "Subir imagen de producto", 
               description = "Sube una imagen principal para un producto a Cloudflare R2")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen subida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido o error en la subida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/product/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadProductImage(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId,
            @Parameter(description = "Archivo de imagen", 
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("image") MultipartFile image) {
        
        log.info("POST /images/product/{} - Subiendo imagen de producto", productId);
        
        try {
            String imageUrl = imageService.uploadProductImage(image, productId);
            
            log.info("Imagen de producto subida exitosamente: {}", imageUrl);
            return ResponseEntity.ok(ImageUploadResponse.success(imageUrl));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación subiendo imagen de producto {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest()
                .body(ImageUploadResponse.error("Error de validación", e.getMessage()));
        } catch (Exception e) {
            log.error("Error interno subiendo imagen de producto {}: {}", productId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ImageUploadResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    @Operation(summary = "Subir múltiples imágenes de producto", 
               description = "Sube múltiples imágenes para un producto a Cloudflare R2")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imágenes subidas exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivos inválidos o error en la subida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/product/{productId}/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadProductImages(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId,
            @Parameter(description = "Archivos de imágenes", 
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("images") MultipartFile[] images) {
        
        log.info("POST /images/product/{}/multiple - Subiendo {} imágenes de producto", 
                productId, images.length);
        
        try {
            List<String> imageUrls = imageService.uploadProductImages(Arrays.asList(images), productId);
            
            log.info("Imágenes de producto subidas exitosamente: {}", imageUrls.size());
            return ResponseEntity.ok(ImageUploadResponse.success(imageUrls));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación subiendo imágenes de producto {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest()
                .body(ImageUploadResponse.error("Error de validación", e.getMessage()));
        } catch (Exception e) {
            log.error("Error interno subiendo imágenes de producto {}: {}", productId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ImageUploadResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    @Operation(summary = "Subir imagen de variante", 
               description = "Sube una imagen específica para una variante de producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen de variante subida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido o error en la subida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/variant/{productId}/{variantSku}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadVariantImage(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId,
            @Parameter(description = "SKU de la variante")
            @PathVariable String variantSku,
            @Parameter(description = "Archivo de imagen", 
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("image") MultipartFile image) {
        
        log.info("POST /images/variant/{}/{} - Subiendo imagen de variante", productId, variantSku);
        
        try {
            String imageUrl = imageService.uploadVariantImage(image, productId, variantSku);
            
            log.info("Imagen de variante subida exitosamente: {}", imageUrl);
            return ResponseEntity.ok(ImageUploadResponse.success(imageUrl));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación subiendo imagen de variante {}/{}: {}", 
                    productId, variantSku, e.getMessage());
            return ResponseEntity.badRequest()
                .body(ImageUploadResponse.error("Error de validación", e.getMessage()));
        } catch (Exception e) {
            log.error("Error interno subiendo imagen de variante {}/{}: {}", 
                    productId, variantSku, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ImageUploadResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar imagen", 
               description = "Elimina una imagen del storage de Cloudflare R2")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen eliminada exitosamente"),
        @ApiResponse(responseCode = "400", description = "URL de imagen inválida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping
    public ResponseEntity<ImageUploadResponse> deleteImage(
            @Parameter(description = "URL completa de la imagen a eliminar")
            @RequestParam String imageUrl) {
        
        log.info("DELETE /images - Eliminando imagen: {}", imageUrl);
        
        try {
            imageService.deleteImage(imageUrl);
            
            log.info("Imagen eliminada exitosamente: {}", imageUrl);
            return ResponseEntity.ok(ImageUploadResponse.builder()
                .success(true)
                .message("Imagen eliminada exitosamente")
                .build());
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación eliminando imagen {}: {}", imageUrl, e.getMessage());
            return ResponseEntity.badRequest()
                .body(ImageUploadResponse.error("Error de validación", e.getMessage()));
        } catch (Exception e) {
            log.error("Error interno eliminando imagen {}: {}", imageUrl, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ImageUploadResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    @Operation(summary = "Verificar existencia de imagen", 
               description = "Verifica si una imagen existe en el storage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación completada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/exists")
    public ResponseEntity<ImageUploadResponse> checkImageExists(
            @Parameter(description = "URL completa de la imagen a verificar")
            @RequestParam String imageUrl) {
        
        log.info("GET /images/exists - Verificando existencia de imagen: {}", imageUrl);
        
        try {
            boolean exists = imageService.imageExists(imageUrl);
            
            return ResponseEntity.ok(ImageUploadResponse.builder()
                .success(true)
                .message(exists ? "La imagen existe" : "La imagen no existe")
                .details(String.valueOf(exists))
                .build());
            
        } catch (Exception e) {
            log.error("Error verificando existencia de imagen {}: {}", imageUrl, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ImageUploadResponse.error("Error interno del servidor", e.getMessage()));
        }
    }
}