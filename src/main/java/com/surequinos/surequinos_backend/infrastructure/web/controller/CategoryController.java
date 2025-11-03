package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.CategoryDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateCategoryRequest;
import com.surequinos.surequinos_backend.application.service.CategoryService;
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
 * Controlador REST para gestión de categorías
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categorías", description = "API para gestión de categorías de productos")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Obtener todas las categorías principales", 
               description = "Retorna todas las categorías principales con sus subcategorías")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorías obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getMainCategories() {
        log.info("GET /categories - Obteniendo categorías principales");
        
        List<CategoryDto> categories = categoryService.getMainCategories();
        
        log.info("Retornando {} categorías principales", categories.size());
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Obtener categorías con conteo de productos", 
               description = "Retorna categorías principales con el número de productos en cada una")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorías con conteo obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/with-product-count")
    public ResponseEntity<List<CategoryDto>> getMainCategoriesWithProductCount() {
        log.info("GET /categories/with-product-count - Obteniendo categorías con conteo");
        
        List<CategoryDto> categories = categoryService.getMainCategoriesWithProductCount();
        
        log.info("Retornando {} categorías con conteo de productos", categories.size());
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Obtener categoría por slug", 
               description = "Busca una categoría específica por su slug único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryDto> getCategoryBySlug(
            @Parameter(description = "Slug único de la categoría", example = "sillas-de-montar")
            @PathVariable String slug) {
        log.info("GET /categories/slug/{} - Buscando categoría por slug", slug);
        
        return categoryService.getCategoryBySlug(slug)
            .map(category -> {
                log.info("Categoría encontrada: {}", category.getName());
                return ResponseEntity.ok(category);
            })
            .orElseGet(() -> {
                log.warn("Categoría no encontrada con slug: {}", slug);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener categoría por ID", 
               description = "Busca una categoría específica por su ID único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(
            @Parameter(description = "ID único de la categoría")
            @PathVariable UUID id) {
        log.info("GET /categories/{} - Buscando categoría por ID", id);
        
        return categoryService.getCategoryById(id)
            .map(category -> {
                log.info("Categoría encontrada: {}", category.getName());
                return ResponseEntity.ok(category);
            })
            .orElseGet(() -> {
                log.warn("Categoría no encontrada con ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Crear nueva categoría", 
               description = "Crea una nueva categoría de productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe una categoría con el mismo slug"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Parameter(description = "Datos de la nueva categoría")
            @Valid @RequestBody CreateCategoryRequest request) {
        log.info("POST /categories - Creando nueva categoría: {}", request.getName());
        
        try {
            CategoryDto createdCategory = categoryService.createCategory(request);
            
            log.info("Categoría creada exitosamente: {} (ID: {})", 
                    createdCategory.getName(), createdCategory.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creando categoría: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar categoría existente", 
               description = "Actualiza los datos de una categoría existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "409", description = "Ya existe una categoría con el mismo slug"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @Parameter(description = "ID único de la categoría")
            @PathVariable UUID id,
            @Parameter(description = "Nuevos datos de la categoría")
            @Valid @RequestBody CreateCategoryRequest request) {
        log.info("PUT /categories/{} - Actualizando categoría", id);
        
        try {
            CategoryDto updatedCategory = categoryService.updateCategory(id, request);
            
            log.info("Categoría actualizada exitosamente: {} (ID: {})", 
                    updatedCategory.getName(), updatedCategory.getId());
            
            return ResponseEntity.ok(updatedCategory);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando categoría {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar categoría", 
               description = "Elimina una categoría existente (solo si no tiene productos o subcategorías)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar la categoría (tiene productos o subcategorías)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID único de la categoría")
            @PathVariable UUID id) {
        log.info("DELETE /categories/{} - Eliminando categoría", id);
        
        try {
            categoryService.deleteCategory(id);
            
            log.info("Categoría eliminada exitosamente: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Categoría no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("No se puede eliminar categoría {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Obtener jerarquía completa de categorías", 
               description = "Retorna la estructura jerárquica completa de todas las categorías")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jerarquía obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/hierarchy")
    public ResponseEntity<List<Object[]>> getCategoryHierarchy() {
        log.info("GET /categories/hierarchy - Obteniendo jerarquía completa");
        
        List<Object[]> hierarchy = categoryService.getCategoryHierarchy();
        
        log.info("Retornando jerarquía con {} elementos", hierarchy.size());
        return ResponseEntity.ok(hierarchy);
    }
}