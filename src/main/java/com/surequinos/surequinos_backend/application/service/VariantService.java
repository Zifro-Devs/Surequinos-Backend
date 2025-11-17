package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.VariantDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateVariantRequest;
import com.surequinos.surequinos_backend.application.mapper.VariantMapper;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import com.surequinos.surequinos_backend.infrastructure.repository.ProductRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de variantes de productos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VariantService {

    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final VariantMapper variantMapper;

    /**
     * Obtiene todas las variantes activas de un producto
     */
    public List<VariantDto> getVariantsByProductId(UUID productId) {
        log.debug("Obteniendo variantes del producto ID: {}", productId);
        
        List<Variant> variants = variantRepository.findActiveVariantsByProductId(productId);
        return variantMapper.toDtoList(variants);
    }

    /**
     * Obtiene variantes disponibles (con stock) de un producto
     */
    public List<VariantDto> getAvailableVariantsByProductId(UUID productId) {
        log.debug("Obteniendo variantes disponibles del producto ID: {}", productId);
        
        List<Variant> variants = variantRepository.findAvailableVariantsByProductId(productId);
        return variantMapper.toDtoList(variants);
    }

    /**
     * Busca una variante por su SKU
     */
    public Optional<VariantDto> getVariantBySku(String sku) {
        log.debug("Buscando variante por SKU: {}", sku);
        
        return variantRepository.findBySku(sku)
            .map(variantMapper::toDto);
    }

    /**
     * Obtiene una variante por ID
     */
    public Optional<VariantDto> getVariantById(UUID id) {
        log.debug("Buscando variante por ID: {}", id);
        
        return variantRepository.findById(id)
            .map(variantMapper::toDto);
    }

    /**
     * Busca variantes por atributos específicos
     */
    public List<VariantDto> getVariantsByAttributes(UUID productId, String color, String size) {
        log.debug("Buscando variantes por atributos - Producto: {}, Color: {}, Talla: {}", 
                 productId, color, size);
        
        List<Variant> variants = variantRepository.findVariantsByAttributes(productId, color, size);
        return variantMapper.toDtoList(variants);
    }

    /**
     * Crea una nueva variante
     */
    @Transactional
    public VariantDto createVariant(CreateVariantRequest request) {
        log.debug("Creando nueva variante: {}", request.getSku());
        
        // Validar que el SKU sea único
        if (variantRepository.existsBySkuAndIdNot(request.getSku(), null)) {
            throw new IllegalArgumentException("Ya existe una variante con el SKU: " + request.getSku());
        }
        
        // Validar que el producto exista
        if (!productRepository.existsById(request.getProductId())) {
            throw new IllegalArgumentException("El producto no existe: " + request.getProductId());
        }
        
        Variant variant = variantMapper.toEntity(request);
        
        // FORZAR: Setear manualmente los atributos en el JSON
        if (request.getColor() != null) {
            variant.setColor(request.getColor());
        }
        if (request.getSize() != null) {
            variant.setSize(request.getSize());
        }
        
        // Asegurar que imageUrl se setee si viene en el request
        if (request.getImageUrl() != null) {
            variant.setImageUrl(request.getImageUrl());
        }
        
        Variant savedVariant = variantRepository.save(variant);
        
        log.info("Variante creada exitosamente: {} (ID: {}) con attributes: {}", 
                savedVariant.getSku(), savedVariant.getId(), savedVariant.getAttributes());
        
        return variantMapper.toDto(savedVariant);
    }

    /**
     * Actualiza una variante existente
     */
    @Transactional
    public VariantDto updateVariant(UUID id, CreateVariantRequest request) {
        log.debug("Actualizando variante ID: {}", id);
        
        Variant existingVariant = variantRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada: " + id));
        
        // Validar que el SKU sea único (excluyendo la variante actual)
        if (variantRepository.existsBySkuAndIdNot(request.getSku(), id)) {
            throw new IllegalArgumentException("Ya existe una variante con el SKU: " + request.getSku());
        }
        
        // Validar que el producto exista
        if (!productRepository.existsById(request.getProductId())) {
            throw new IllegalArgumentException("El producto no existe: " + request.getProductId());
        }
        
        // Actualizar campos
        existingVariant.setProductId(request.getProductId());
        existingVariant.setSku(request.getSku());
        existingVariant.setColor(request.getColor());
        existingVariant.setSize(request.getSize());
        existingVariant.setPrice(request.getPrice());
        existingVariant.setStock(request.getStock());
        existingVariant.setImageUrl(request.getImageUrl());
        existingVariant.setIsActive(request.getIsActive());
        
        Variant savedVariant = variantRepository.save(existingVariant);
        
        log.info("Variante actualizada exitosamente: {} (ID: {})", savedVariant.getSku(), savedVariant.getId());
        
        return variantMapper.toDto(savedVariant);
    }

    /**
     * Elimina una variante
     */
    @Transactional
    public void deleteVariant(UUID id) {
        log.debug("Eliminando variante ID: {}", id);
        
        Variant variant = variantRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada: " + id));
        
        variantRepository.delete(variant);
        
        log.info("Variante eliminada exitosamente: {} (ID: {})", variant.getSku(), variant.getId());
    }

    /**
     * Actualiza el stock de una variante
     */
    @Transactional
    public VariantDto updateStock(UUID variantId, Integer newStock) {
        log.debug("Actualizando stock de variante ID: {} a {}", variantId, newStock);
        
        Variant variant = variantRepository.findById(variantId)
            .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada: " + variantId));
        
        variant.setStock(newStock);
        Variant savedVariant = variantRepository.save(variant);
        
        log.info("Stock actualizado exitosamente para variante: {} (Nuevo stock: {})", 
                savedVariant.getSku(), newStock);
        
        return variantMapper.toDto(savedVariant);
    }

    /**
     * Reduce el stock de una variante (para ventas)
     */
    @Transactional
    public boolean reduceStock(UUID variantId, Integer quantity) {
        log.debug("Reduciendo stock de variante ID: {} en {} unidades", variantId, quantity);
        
        int updatedRows = variantRepository.reduceStock(variantId, quantity);
        
        if (updatedRows > 0) {
            log.info("Stock reducido exitosamente para variante ID: {} en {} unidades", variantId, quantity);
            return true;
        } else {
            log.warn("No se pudo reducir el stock para variante ID: {} (stock insuficiente)", variantId);
            return false;
        }
    }

    /**
     * Obtiene el rango de precios de un producto
     */
    public Object[] getPriceRangeByProductId(UUID productId) {
        log.debug("Obteniendo rango de precios para producto ID: {}", productId);
        return variantRepository.getPriceRangeByProductId(productId);
    }

    /**
     * Obtiene el stock total de un producto
     */
    public Integer getTotalStockByProductId(UUID productId) {
        log.debug("Obteniendo stock total para producto ID: {}", productId);
        return variantRepository.getTotalStockByProductId(productId);
    }

    /**
     * Obtiene variantes con stock bajo
     */
    public List<Object[]> getVariantsWithLowStock(Integer threshold) {
        log.debug("Obteniendo variantes con stock bajo (threshold: {})", threshold);
        return variantRepository.findVariantsWithLowStock(threshold);
    }

    /**
     * Obtiene colores disponibles para un producto
     */
    public List<String> getAvailableColorsByProductId(UUID productId) {
        log.debug("Obteniendo colores disponibles para producto ID: {}", productId);
        return variantRepository.findAvailableColorsByProductId(productId);
    }

    /**
     * Obtiene tallas disponibles para un producto y color específico
     */
    public List<String> getAvailableSizesByProductIdAndColor(UUID productId, String color) {
        log.debug("Obteniendo tallas disponibles para producto ID: {} y color: {}", productId, color);
        return variantRepository.findAvailableSizesByProductIdAndColor(productId, color);
    }

}