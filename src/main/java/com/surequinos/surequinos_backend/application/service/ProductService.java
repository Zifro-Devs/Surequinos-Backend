package com.surequinos.surequinos_backend.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surequinos.surequinos_backend.application.dto.ProductDto;
import com.surequinos.surequinos_backend.application.dto.VariantDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateProductRequest;
import com.surequinos.surequinos_backend.application.mapper.ProductMapper;
import com.surequinos.surequinos_backend.application.mapper.VariantMapper;
import com.surequinos.surequinos_backend.domain.entity.Product;
import com.surequinos.surequinos_backend.infrastructure.repository.CategoryRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.ProductRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de productos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final VariantMapper variantMapper;
    private final ObjectMapper objectMapper;

    /**
     * Obtiene todos los productos activos con paginación
     */
    public Page<ProductDto> getActiveProducts(Pageable pageable) {
        log.debug("Obteniendo productos activos con paginación: {}", pageable);
        
        Page<Product> products = productRepository.findActiveProducts(pageable);
        return products.map(this::enrichProductDto);
    }

    /**
     * Obtiene productos usando la vista completa (v_products_full)
     */
    public List<ProductDto> getProductsFullView() {
        log.debug("Obteniendo productos usando vista completa");
        
        List<Object[]> results = productRepository.findProductsFullView();
        return results.stream()
            .map(this::mapProductFullView)
            .toList();
    }

    /**
     * Busca un producto por su slug usando la vista completa
     */
    public Optional<ProductDto> getProductBySlug(String slug) {
        log.debug("Buscando producto por slug: {}", slug);
        
        return productRepository.findProductFullBySlug(slug)
            .map(this::mapProductFullView);
    }

    /**
     * Obtiene un producto por ID
     */
    public Optional<ProductDto> getProductById(UUID id) {
        log.debug("Buscando producto por ID: {}", id);
        
        return productRepository.findById(id)
            .map(this::enrichProductDto);
    }

    /**
     * Busca productos por categoría
     */
    public Page<ProductDto> getProductsByCategory(UUID categoryId, Pageable pageable) {
        log.debug("Buscando productos por categoría ID: {}", categoryId);
        
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::enrichProductDto);
    }

    /**
     * Busca productos por slug de categoría
     */
    public List<ProductDto> getProductsByCategorySlug(String categorySlug) {
        log.debug("Buscando productos por slug de categoría: {}", categorySlug);
        
        List<Object[]> results = productRepository.findProductsByCategorySlug(categorySlug);
        return results.stream()
            .map(this::mapProductFullView)
            .toList();
    }

    /**
     * Busca productos por texto
     */
    public Page<ProductDto> searchProducts(String searchText, Pageable pageable) {
        log.debug("Buscando productos por texto: {}", searchText);
        
        Page<Product> products = productRepository.findBySearchText(searchText, pageable);
        return products.map(this::enrichProductDto);
    }

    /**
     * Crea un nuevo producto
     */
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        log.debug("Creando nuevo producto: {}", request.getName());
        
        // Validar que el slug sea único
        if (productRepository.existsBySlugAndIdNot(request.getSlug(), null)) {
            throw new IllegalArgumentException("Ya existe un producto con el slug: " + request.getSlug());
        }
        
        // Validar que la categoría exista
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new IllegalArgumentException("La categoría no existe: " + request.getCategoryId());
        }
        
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        
        log.info("Producto creado exitosamente: {} (ID: {})", savedProduct.getName(), savedProduct.getId());
        
        return enrichProductDto(savedProduct);
    }

    /**
     * Actualiza un producto existente
     */
    @Transactional
    public ProductDto updateProduct(UUID id, CreateProductRequest request) {
        log.debug("Actualizando producto ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        
        // Validar que el slug sea único (excluyendo el producto actual)
        if (productRepository.existsBySlugAndIdNot(request.getSlug(), id)) {
            throw new IllegalArgumentException("Ya existe un producto con el slug: " + request.getSlug());
        }
        
        // Validar que la categoría exista
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new IllegalArgumentException("La categoría no existe: " + request.getCategoryId());
        }
        
        // Actualizar campos
        existingProduct.setCategoryId(request.getCategoryId());
        existingProduct.setName(request.getName());
        existingProduct.setSlug(request.getSlug());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setImages(request.getImages());
        existingProduct.setBasePrice(request.getBasePrice());
        existingProduct.setIsActive(request.getIsActive());
        
        Product savedProduct = productRepository.save(existingProduct);
        
        log.info("Producto actualizado exitosamente: {} (ID: {})", savedProduct.getName(), savedProduct.getId());
        
        return enrichProductDto(savedProduct);
    }

    /**
     * Elimina un producto
     */
    @Transactional
    public void deleteProduct(UUID id) {
        log.debug("Eliminando producto ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        
        productRepository.delete(product);
        
        log.info("Producto eliminado exitosamente: {} (ID: {})", product.getName(), product.getId());
    }

    /**
     * Obtiene productos con stock bajo
     */
    public List<ProductDto> getProductsWithLowStock(Integer threshold) {
        log.debug("Obteniendo productos con stock bajo (threshold: {})", threshold);
        
        List<Product> products = productRepository.findProductsWithLowStock(threshold);
        return products.stream()
            .map(this::enrichProductDto)
            .toList();
    }

    /**
     * Obtiene estadísticas de productos por categoría
     */
    public List<Object[]> getProductStatsByCategory() {
        log.debug("Obteniendo estadísticas de productos por categoría");
        return productRepository.getProductStatsByCategory();
    }

    /**
     * Enriquece un ProductDto con información adicional de variantes
     */
    private ProductDto enrichProductDto(Product product) {
        ProductDto dto = productMapper.toDto(product);
        
        // Procesar imágenes del producto
        if (product.getImages() != null) {
            String[] processedImages = processImageArray(product.getImages());
            dto.setImages(processedImages);
        }
        
        // Cargar variantes
        List<VariantDto> variants = variantRepository.findActiveVariantsByProductId(product.getId())
            .stream()
            .map(variantMapper::toDto)
            .toList();
        
        dto.setVariants(variants);
        
        // Calcular información agregada
        if (!variants.isEmpty()) {
            BigDecimal minPrice = variants.stream()
                .map(VariantDto::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            BigDecimal maxPrice = variants.stream()
                .map(VariantDto::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            Integer totalStock = variants.stream()
                .mapToInt(VariantDto::getStock)
                .sum();
            
            dto.setMinPrice(minPrice);
            dto.setMaxPrice(maxPrice);
            dto.setTotalStock(totalStock);
            dto.setHasStock(totalStock > 0);
        } else {
            dto.setMinPrice(BigDecimal.ZERO);
            dto.setMaxPrice(BigDecimal.ZERO);
            dto.setTotalStock(0);
            dto.setHasStock(false);
        }
        
        // Cargar información de categoría
        if (product.getCategory() != null) {
            dto.setCategory(product.getCategory().getName());
            dto.setCategorySlug(product.getCategory().getSlug());
        }
        
        // Asegurar que categoryId esté presente
        dto.setCategoryId(product.getCategoryId());
        
        return dto;
    }

    /**
     * Mapea el resultado de la vista v_products_full a ProductDto
     */
    private ProductDto mapProductFullView(Object[] result) {
        try {
            ProductDto dto = ProductDto.builder()
                .id((UUID) result[0])
                .name((String) result[1])
                .slug((String) result[2])
                .description((String) result[3])
                .basePrice((BigDecimal) result[5])
                .isActive((Boolean) result[6])
                .createdAt(((java.sql.Timestamp) result[7]).toLocalDateTime())
                .categoryId((UUID) result[8])
                .category((String) result[9])
                .categorySlug((String) result[10])
                .build();
            
            // Procesar imágenes del producto
            Object imagesObj = result[4];
            if (imagesObj != null) {
                String[] productImages = processImageArray(imagesObj);
                dto.setImages(productImages);
            }
            
            // Parsear variantes desde JSON
            String variantsJson = (String) result[11];
            if (variantsJson != null && !variantsJson.equals("[]")) {
                List<VariantDto> variants = objectMapper.readValue(
                    variantsJson, 
                    new TypeReference<List<VariantDto>>() {}
                );
                
                dto.setVariants(variants);
            } else {
                dto.setVariants(List.of());
            }
            
            // Usar información agregada pre-calculada de la vista
            dto.setMinPrice((BigDecimal) result[12]);
            dto.setMaxPrice((BigDecimal) result[13]);
            dto.setTotalStock(((Number) result[14]).intValue());
            dto.setHasStock((Boolean) result[15]);
            
            return dto;
            
        } catch (JsonProcessingException e) {
            log.error("Error parseando variantes JSON para producto {}: {}", result[0], e.getMessage());
            throw new RuntimeException("Error procesando datos del producto", e);
        } catch (Exception e) {
            log.error("Error mapeando producto desde vista: {}", e.getMessage());
            throw new RuntimeException("Error procesando datos del producto", e);
        }
    }

    /**
     * Procesa un array de imágenes desde PostgreSQL
     */
    private String[] processImageArray(Object imagesObj) {
        if (imagesObj == null) {
            return new String[0];
        }

        log.debug("Procesando imágenes - Tipo: {}, Valor: {}", imagesObj.getClass().getSimpleName(), imagesObj);

        String[] productImages;
        
        if (imagesObj instanceof String[]) {
            productImages = (String[]) imagesObj;
        } else if (imagesObj instanceof String) {
            String imagesStr = (String) imagesObj;
            
            // Manejar formato PostgreSQL array: {url1,url2,url3}
            if (imagesStr.startsWith("{") && imagesStr.endsWith("}")) {
                imagesStr = imagesStr.substring(1, imagesStr.length() - 1);
                if (imagesStr.trim().isEmpty()) {
                    return new String[0];
                }
                // Split por comas, pero cuidado con URLs que pueden tener comas
                productImages = imagesStr.split(",(?=https?://)");
            } else {
                productImages = new String[]{imagesStr};
            }
        } else {
            log.warn("Tipo de imagen no reconocido: {}", imagesObj.getClass());
            return new String[0];
        }
        
        // Limpiar y validar URLs
        String[] cleanedImages = Arrays.stream(productImages)
            .filter(Objects::nonNull)
            .map(String::trim)
            .map(url -> url.replaceAll("^\"|\"$", "")) // Remover comillas
            .filter(url -> !url.isEmpty())
            .filter(url -> url.startsWith("http")) // Solo URLs válidas
            .toArray(String[]::new);
        
        log.debug("Imágenes procesadas: {}", Arrays.toString(cleanedImages));
        return cleanedImages;
    }


}