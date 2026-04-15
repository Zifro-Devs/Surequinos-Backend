package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.request.ApplyDiscountRequest;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import com.surequinos.surequinos_backend.infrastructure.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PromotionService {

    private final VariantRepository variantRepository;

    /**
     * Aplica un descuento a una variante específica
     */
    @Transactional
    public Variant applyDiscount(ApplyDiscountRequest request) {
        log.debug("Aplicando descuento del {}% a variante: {}", request.getDiscountPercentage(), request.getVariantId());

        Variant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada: " + request.getVariantId()));

        variant.setDiscountPercentage(request.getDiscountPercentage());
        variant.setDiscountStartDate(request.getStartDate());
        variant.setDiscountEndDate(request.getEndDate());

        Variant savedVariant = variantRepository.save(variant);

        log.info("Descuento aplicado exitosamente a variante {} (SKU: {})", savedVariant.getId(), savedVariant.getSku());

        return savedVariant;
    }

    /**
     * Remueve el descuento de una variante
     */
    @Transactional
    public Variant removeDiscount(UUID variantId) {
        log.debug("Removiendo descuento de variante: {}", variantId);

        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada: " + variantId));

        variant.setDiscountPercentage(null);
        variant.setDiscountStartDate(null);
        variant.setDiscountEndDate(null);

        Variant savedVariant = variantRepository.save(variant);

        log.info("Descuento removido de variante {} (SKU: {})", savedVariant.getId(), savedVariant.getSku());

        return savedVariant;
    }

    /**
     * Obtiene todas las variantes con descuento activo
     */
    public List<Variant> getVariantsWithActiveDiscounts() {
        log.debug("Obteniendo variantes con descuentos activos");

        List<Variant> allVariants = variantRepository.findAll();
        
        List<Variant> activeDiscounts = allVariants.stream()
                .filter(Variant::hasActiveDiscount)
                .toList();
        
        log.info("Se encontraron {} variantes con descuentos activos", activeDiscounts.size());
        
        return activeDiscounts;
    }

    /**
     * Obtiene todas las variantes con algún descuento configurado (activo o no)
     */
    public List<Variant> getVariantsWithDiscounts() {
        log.debug("Obteniendo variantes con descuentos configurados");

        return variantRepository.findAll().stream()
                .filter(v -> v.getDiscountPercentage() != null && v.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }
}
