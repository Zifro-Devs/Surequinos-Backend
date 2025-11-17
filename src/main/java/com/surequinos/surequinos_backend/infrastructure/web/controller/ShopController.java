package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.CategoryDto;
import com.surequinos.surequinos_backend.application.dto.ProductDto;
import com.surequinos.surequinos_backend.application.dto.VariantDto;
import com.surequinos.surequinos_backend.application.dto.response.ProductFiltersDto;
import com.surequinos.surequinos_backend.application.service.CategoryService;
import com.surequinos.surequinos_backend.application.service.ProductService;
import com.surequinos.surequinos_backend.application.service.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controlador específico para endpoints de la tienda (frontend)
 * Optimizado para las necesidades específicas del ecommerce
 */
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tienda", description = "API optimizada para el frontend de la tienda")
public class ShopController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final VariantService variantService;

    @Operation(summary = "Obtener productos para la tienda", 
               description = "Endpoint optimizado para mostrar productos en la tienda con filtros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getShopProducts(
            @Parameter(description = "Slug de categoría para filtrar (opcional)")
            @RequestParam(required = false) String category,
            @Parameter(description = "Texto de búsqueda (opcional)")
            @RequestParam(required = false) String search,
            @Parameter(description = "Color para filtrar (opcional)")
            @RequestParam(required = false) String color,
            @Parameter(description = "Talla para filtrar (opcional)")
            @RequestParam(required = false) String size,
            @Parameter(description = "Tipo para filtrar (opcional)")
            @RequestParam(required = false) String type,
            @Parameter(description = "Precio mínimo (opcional)")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Precio máximo (opcional)")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Parámetros de paginación")
            @PageableDefault(size = 12) Pageable pageable) {
        
        log.info("GET /shop/products - Obteniendo productos para tienda (categoría: {}, búsqueda: {})", 
                category, search);

        Page<ProductDto> products;

        if (search != null && !search.trim().isEmpty()) {
            // Búsqueda por texto
            products = productService.searchProducts(search.trim(), pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            // Filtro por categoría
            products = productService.getProductsByCategorySlug(category)
                .stream()
                .collect(java.util.stream.Collectors.collectingAndThen(
                    java.util.stream.Collectors.toList(),
                    list -> new org.springframework.data.domain.PageImpl<>(
                        list.subList(
                            Math.min((int) pageable.getOffset(), list.size()),
                            Math.min((int) pageable.getOffset() + pageable.getPageSize(), list.size())
                        ),
                        pageable,
                        list.size()
                    )
                ));
        } else {
            // Todos los productos activos
            products = productService.getActiveProducts(pageable);
        }

        log.info("Retornando {} productos para la tienda", products.getNumberOfElements());
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener categorías para navegación", 
               description = "Retorna categorías principales con conteo de productos para el menú de navegación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorías obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getShopCategories() {
        log.info("GET /shop/categories - Obteniendo categorías para navegación");
        
        List<CategoryDto> categories = categoryService.getMainCategoriesWithProductCount();
        
        log.info("Retornando {} categorías para navegación", categories.size());
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Obtener filtros disponibles", 
               description = "Retorna todos los filtros disponibles para productos (colores, tallas, tipos, precios)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filtros obtenidos exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/filters")
    public ResponseEntity<ProductFiltersDto> getProductFilters(
            @Parameter(description = "Slug de categoría para filtrar opciones (opcional)")
            @RequestParam(required = false) String category) {
        log.info("GET /shop/filters - Obteniendo filtros disponibles (categoría: {})", category);
        
        // Por simplicidad, retornamos filtros básicos
        // En una implementación completa, esto se optimizaría con queries específicas
        ProductFiltersDto filters = ProductFiltersDto.builder()
            .colors(List.of("Roble", "Chocolate", "Negro", "Blanco", "Rojo", "Azul"))
            .sizes(List.of("12\"", "12.5\"", "13\"", "13.5\"", "14\"", "14.5\"", "15\"", "15.5\"", "16\"", "17\""))
            .types(List.of("Americana", "Trenzada", "Nacional", "Sencillo", "De Lujo", "Timbiano"))
            .minPrice(BigDecimal.valueOf(50000))
            .maxPrice(BigDecimal.valueOf(3000000))
            .categories(categoryService.getMainCategoriesWithProductCount()
                .stream()
                .map(cat -> ProductFiltersDto.CategoryFilterDto.builder()
                    .name(cat.getName())
                    .slug(cat.getSlug())
                    .productCount(cat.getProductCount())
                    .build())
                .toList())
            .build();
        
        log.info("Retornando filtros con {} colores, {} tallas, {} tipos y {} categorías", 
                filters.getColors().size(), filters.getSizes().size(), 
                filters.getTypes().size(), filters.getCategories().size());
        
        return ResponseEntity.ok(filters);
    }

    @Operation(summary = "Obtener producto completo para detalle", 
               description = "Retorna información completa de un producto incluyendo todas sus variantes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/products/{slug}")
    public ResponseEntity<ProductDto> getProductDetail(
            @Parameter(description = "Slug único del producto")
            @PathVariable String slug) {
        log.info("GET /shop/products/{} - Obteniendo detalle del producto", slug);
        
        return productService.getProductBySlug(slug)
            .map(product -> {
                log.info("Producto encontrado para detalle: {}", product.getName());
                return ResponseEntity.ok(product);
            })
            .orElseGet(() -> {
                log.warn("Producto no encontrado para detalle: {}", slug);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener opciones de variantes para un producto", 
               description = "Retorna las opciones disponibles de colores, tallas y tipos para un producto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Opciones obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/products/{productId}/variant-options")
    public ResponseEntity<VariantOptionsDto> getVariantOptions(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId) {
        log.info("GET /shop/products/{}/variant-options - Obteniendo opciones de variantes", productId);
        
        VariantOptionsDto options = VariantOptionsDto.builder()
            .colors(variantService.getAvailableColorsByProductId(productId))
            .sizes(variantService.getAvailableSizesByProductIdAndColor(productId, null))
            .build();
        
        log.info("Retornando opciones: {} colores, {} tallas", 
                options.getColors().size(), options.getSizes().size());
        
        return ResponseEntity.ok(options);
    }

    @Operation(summary = "Obtener imagen de variante específica", 
               description = "Retorna la imagen específica de una variante basada en sus atributos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen de variante obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/products/{productId}/variant-image")
    public ResponseEntity<VariantImageDto> getVariantImage(
            @Parameter(description = "ID del producto")
            @PathVariable UUID productId,
            @Parameter(description = "Color de la variante")
            @RequestParam(required = false) String color,
            @Parameter(description = "Talla de la variante")
            @RequestParam(required = false) String size) {
        
        log.info("GET /shop/products/{}/variant-image - Obteniendo imagen de variante (color: {}, talla: {})", 
                productId, color, size);
        
        // Buscar variantes que coincidan con los atributos
        List<VariantDto> matchingVariants = variantService.getVariantsByAttributes(productId, color, size);
        
        if (matchingVariants.isEmpty()) {
            log.warn("No se encontraron variantes para los atributos especificados");
            return ResponseEntity.notFound().build();
        }
        
        // Buscar la primera variante que tenga imagen específica
        VariantDto variantWithImage = matchingVariants.stream()
            .filter(v -> v.getImageUrl() != null && !v.getImageUrl().isEmpty())
            .findFirst()
            .orElse(matchingVariants.get(0)); // Si ninguna tiene imagen, usar la primera
        
        VariantImageDto response = VariantImageDto.builder()
            .variantId(variantWithImage.getId())
            .sku(variantWithImage.getSku())
            .imageUrl(variantWithImage.getImageUrl())
            .color(variantWithImage.getColor())
            .size(variantWithImage.getSize())
            .price(variantWithImage.getPrice())
            .stock(variantWithImage.getStock())
            .available(variantWithImage.getAvailable())
            .build();
        
        log.info("Retornando imagen de variante: {} ({})", variantWithImage.getSku(), variantWithImage.getImageUrl());
        return ResponseEntity.ok(response);
    }

    /**
     * DTO para opciones de variantes
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class VariantOptionsDto {
        private List<String> colors;
        private List<String> sizes;
    }

    /**
     * DTO para imagen de variante específica
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class VariantImageDto {
        private UUID variantId;
        private String sku;
        private String imageUrl;
        private String color;
        private String size;
        private BigDecimal price;
        private Integer stock;
        private Boolean available;
    }
}