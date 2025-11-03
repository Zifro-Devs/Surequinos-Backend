package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surequinos.surequinos_backend.application.dto.ProductDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateProductRequest;
import com.surequinos.surequinos_backend.application.dto.request.CreateProductWithImagesRequest;
import com.surequinos.surequinos_backend.application.service.ImageService;
import com.surequinos.surequinos_backend.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador REST para gestión de productos
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Productos", description = "API para gestión de productos del ecommerce")
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Obtener productos activos con paginación", 
               description = "Retorna una página de productos activos ordenados por fecha de creación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getActiveProducts(
            @Parameter(description = "Parámetros de paginación")
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /products - Obteniendo productos activos (página: {}, tamaño: {})", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ProductDto> products = productService.getActiveProducts(pageable);
        
        log.info("Retornando {} productos de {} totales", 
                products.getNumberOfElements(), products.getTotalElements());
        
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener productos usando vista completa", 
               description = "Retorna todos los productos con información completa incluyendo variantes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/full")
    public ResponseEntity<List<ProductDto>> getProductsFullView() {
        log.info("GET /products/full - Obteniendo productos con vista completa");
        
        List<ProductDto> products = productService.getProductsFullView();
        
        log.info("Retornando {} productos con vista completa", products.size());
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener producto por slug", 
               description = "Busca un producto específico por su slug único con información completa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDto> getProductBySlug(
            @Parameter(description = "Slug único del producto", example = "silla-caucana")
            @PathVariable String slug) {
        log.info("GET /products/slug/{} - Buscando producto por slug", slug);
        
        return productService.getProductBySlug(slug)
            .map(product -> {
                log.info("Producto encontrado: {}", product.getName());
                return ResponseEntity.ok(product);
            })
            .orElseGet(() -> {
                log.warn("Producto no encontrado con slug: {}", slug);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener producto por ID", 
               description = "Busca un producto específico por su ID único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "ID único del producto")
            @PathVariable UUID id) {
        log.info("GET /products/{} - Buscando producto por ID", id);
        
        return productService.getProductById(id)
            .map(product -> {
                log.info("Producto encontrado: {}", product.getName());
                return ResponseEntity.ok(product);
            })
            .orElseGet(() -> {
                log.warn("Producto no encontrado con ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener productos por categoría", 
               description = "Retorna productos de una categoría específica con paginación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @Parameter(description = "ID de la categoría")
            @PathVariable UUID categoryId,
            @Parameter(description = "Parámetros de paginación")
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /products/category/{} - Obteniendo productos por categoría", categoryId);
        
        Page<ProductDto> products = productService.getProductsByCategory(categoryId, pageable);
        
        log.info("Retornando {} productos de la categoría {}", 
                products.getNumberOfElements(), categoryId);
        
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener productos por slug de categoría", 
               description = "Retorna productos de una categoría específica usando su slug")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/category/slug/{categorySlug}")
    public ResponseEntity<List<ProductDto>> getProductsByCategorySlug(
            @Parameter(description = "Slug de la categoría", example = "sillas-de-montar")
            @PathVariable String categorySlug) {
        log.info("GET /products/category/slug/{} - Obteniendo productos por slug de categoría", categorySlug);
        
        List<ProductDto> products = productService.getProductsByCategorySlug(categorySlug);
        
        log.info("Retornando {} productos de la categoría {}", products.size(), categorySlug);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar productos por texto", 
               description = "Busca productos por nombre o descripción con paginación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @Parameter(description = "Texto a buscar en nombre o descripción", example = "silla")
            @RequestParam String q,
            @Parameter(description = "Parámetros de paginación")
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /products/search?q={} - Buscando productos por texto", q);
        
        Page<ProductDto> products = productService.searchProducts(q, pageable);
        
        log.info("Búsqueda '{}' retornó {} productos de {} totales", 
                q, products.getNumberOfElements(), products.getTotalElements());
        
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Crear nuevo producto", 
               description = "Crea un nuevo producto en el catálogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un producto con el mismo slug"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Parameter(description = "Datos del nuevo producto")
            @Valid @RequestBody CreateProductRequest request) {
        log.info("POST /products - Creando nuevo producto: {}", request.getName());
        
        try {
            ProductDto createdProduct = productService.createProduct(request);
            
            log.info("Producto creado exitosamente: {} (ID: {})", 
                    createdProduct.getName(), createdProduct.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creando producto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar producto existente", 
               description = "Actualiza los datos de un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "409", description = "Ya existe un producto con el mismo slug"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "ID único del producto")
            @PathVariable UUID id,
            @Parameter(description = "Nuevos datos del producto")
            @Valid @RequestBody CreateProductRequest request) {
        log.info("PUT /products/{} - Actualizando producto", id);
        
        try {
            ProductDto updatedProduct = productService.updateProduct(id, request);
            
            log.info("Producto actualizado exitosamente: {} (ID: {})", 
                    updatedProduct.getName(), updatedProduct.getId());
            
            return ResponseEntity.ok(updatedProduct);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando producto {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar producto", 
               description = "Elimina un producto del catálogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID único del producto")
            @PathVariable UUID id) {
        log.info("DELETE /products/{} - Eliminando producto", id);
        
        try {
            productService.deleteProduct(id);
            
            log.info("Producto eliminado exitosamente: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Producto no encontrado: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener productos con stock bajo", 
               description = "Retorna productos que tienen variantes con stock por debajo del umbral especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos con stock bajo obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getProductsWithLowStock(
            @Parameter(description = "Umbral de stock bajo", example = "5")
            @RequestParam(defaultValue = "5") Integer threshold) {
        log.info("GET /products/low-stock?threshold={} - Obteniendo productos con stock bajo", threshold);
        
        List<ProductDto> products = productService.getProductsWithLowStock(threshold);
        
        log.info("Retornando {} productos con stock bajo (umbral: {})", products.size(), threshold);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener estadísticas de productos por categoría", 
               description = "Retorna estadísticas agregadas de productos agrupadas por categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/stats/by-category")
    public ResponseEntity<List<Object[]>> getProductStatsByCategory() {
        log.info("GET /products/stats/by-category - Obteniendo estadísticas por categoría");
        
        List<Object[]> stats = productService.getProductStatsByCategory();
        
        log.info("Retornando estadísticas de {} categorías", stats.size());
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Debug - Obtener producto con información detallada de imágenes", 
               description = "Endpoint de debug para verificar cómo se procesan las imágenes")
    @GetMapping("/debug/{id}")
    public ResponseEntity<Map<String, Object>> debugProduct(@PathVariable UUID id) {
        log.info("GET /products/debug/{} - Debug de producto", id);
        
        return productService.getProductById(id)
            .map(product -> {
                Map<String, Object> debug = new HashMap<>();
                debug.put("product", product);
                debug.put("imagesRaw", product.getImages());
                debug.put("imagesType", product.getImages() != null ? product.getImages().getClass().getSimpleName() : "null");
                debug.put("imagesLength", product.getImages() != null ? product.getImages().length : 0);
                
                if (product.getImages() != null) {
                    for (int i = 0; i < product.getImages().length; i++) {
                        debug.put("image_" + i, product.getImages()[i]);
                    }
                }
                
                return ResponseEntity.ok(debug);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear producto con imágenes", 
               description = "Crea un nuevo producto con imágenes que se suben a Cloudflare R2")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente con imágenes"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error en imágenes"),
        @ApiResponse(responseCode = "409", description = "Ya existe un producto con el mismo slug"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> createProductWithImages(
            @Parameter(description = "Datos del producto en formato JSON")
            @RequestPart("product") String productJson,
            @Parameter(description = "Imágenes del producto")
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        
        log.info("POST /products/with-images - Creando producto con imágenes");
        
        try {
            // Parsear el JSON del producto
            CreateProductWithImagesRequest productRequest = objectMapper.readValue(productJson, CreateProductWithImagesRequest.class);
            
            log.info("Creando producto: {}", productRequest.getName());
            
            // Crear el producto primero
            CreateProductRequest createRequest = CreateProductRequest.builder()
                .categoryId(productRequest.getCategoryId())
                .name(productRequest.getName())
                .slug(productRequest.getSlug())
                .description(productRequest.getDescription())
                .basePrice(productRequest.getBasePrice())
                .isActive(productRequest.getIsActive())
                .images(new String[0]) // Inicialmente vacío
                .build();
            
            ProductDto createdProduct = productService.createProduct(createRequest);
            
            // Subir imágenes si se proporcionaron
            if (images != null && images.length > 0) {
                try {
                    List<String> imageUrls = imageService.uploadProductImages(
                        List.of(images), createdProduct.getId());
                    
                    // Actualizar el producto con las URLs de las imágenes
                    createRequest.setImages(imageUrls.toArray(new String[0]));
                    createdProduct = productService.updateProduct(createdProduct.getId(), createRequest);
                    
                    log.info("Producto creado con {} imágenes: {} (ID: {})", 
                            imageUrls.size(), createdProduct.getName(), createdProduct.getId());
                } catch (Exception e) {
                    log.error("Error subiendo imágenes para producto {}: {}", 
                            createdProduct.getId(), e.getMessage());
                    // El producto ya fue creado, pero sin imágenes
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creando producto con imágenes: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno creando producto con imágenes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}