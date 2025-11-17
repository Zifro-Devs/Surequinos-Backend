package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de variantes con queries nativas
 */
@Repository
public interface VariantRepository extends JpaRepository<Variant, UUID> {

    /**
     * Busca una variante por su SKU
     */
    @Query(value = "SELECT * FROM variants WHERE sku = :sku", nativeQuery = true)
    Optional<Variant> findBySku(@Param("sku") String sku);

    /**
     * Obtiene todas las variantes activas de un producto
     */
    @Query(value = """
        SELECT * FROM variants 
        WHERE product_id = :productId AND is_active = true 
        ORDER BY attributes->>'color' ASC, attributes->>'size' ASC
        """, nativeQuery = true)
    List<Variant> findActiveVariantsByProductId(@Param("productId") UUID productId);

    /**
     * Obtiene variantes disponibles (con stock) de un producto
     */
    @Query(value = """
        SELECT * FROM variants 
        WHERE product_id = :productId 
        AND is_active = true 
        AND stock > 0 
        ORDER BY attributes->>'color' ASC, attributes->>'size' ASC
        """, nativeQuery = true)
    List<Variant> findAvailableVariantsByProductId(@Param("productId") UUID productId);

    /**
     * Busca variantes por atributos específicos
     */
    @Query(value = """
        SELECT * FROM variants 
        WHERE product_id = :productId 
        AND is_active = true
        AND (:color IS NULL OR attributes->>'color' = :color)
        AND (:size IS NULL OR attributes->>'size' = :size)
        ORDER BY price ASC
        """, nativeQuery = true)
    List<Variant> findVariantsByAttributes(
        @Param("productId") UUID productId,
        @Param("color") String color,
        @Param("size") String size
    );

    /**
     * Obtiene el rango de precios de un producto
     */
    @Query(value = """
        SELECT 
            MIN(price) as min_price,
            MAX(price) as max_price
        FROM variants 
        WHERE product_id = :productId AND is_active = true
        """, nativeQuery = true)
    Object[] getPriceRangeByProductId(@Param("productId") UUID productId);

    /**
     * Obtiene el stock total de un producto
     */
    @Query(value = """
        SELECT COALESCE(SUM(stock), 0) 
        FROM variants 
        WHERE product_id = :productId AND is_active = true
        """, nativeQuery = true)
    Integer getTotalStockByProductId(@Param("productId") UUID productId);

    /**
     * Verifica si existe una variante con el SKU dado (excluyendo un ID específico)
     */
    @Query(value = """
        SELECT COUNT(*) > 0 FROM variants 
        WHERE sku = :sku AND (:excludeId IS NULL OR id != :excludeId)
        """, nativeQuery = true)
    boolean existsBySkuAndIdNot(@Param("sku") String sku, @Param("excludeId") UUID excludeId);

    /**
     * Actualiza el stock de una variante
     */
    @Modifying
    @Query(value = """
        UPDATE variants 
        SET stock = :newStock 
        WHERE id = :variantId
        """, nativeQuery = true)
    int updateStock(@Param("variantId") UUID variantId, @Param("newStock") Integer newStock);

    /**
     * Reduce el stock de una variante (para ventas)
     */
    @Modifying
    @Query(value = """
        UPDATE variants 
        SET stock = stock - :quantity 
        WHERE id = :variantId AND stock >= :quantity
        """, nativeQuery = true)
    int reduceStock(@Param("variantId") UUID variantId, @Param("quantity") Integer quantity);

    /**
     * Obtiene variantes con stock bajo
     */
    @Query(value = """
        SELECT v.*, p.name as product_name 
        FROM variants v
        INNER JOIN products p ON v.product_id = p.id
        WHERE v.is_active = true 
        AND p.is_active = true 
        AND v.stock < :threshold
        ORDER BY v.stock ASC, p.name ASC
        """, nativeQuery = true)
    List<Object[]> findVariantsWithLowStock(@Param("threshold") Integer threshold);

    /**
     * Obtiene colores disponibles para un producto
     */
    @Query(value = """
        SELECT DISTINCT attributes->>'color' as color
        FROM variants 
        WHERE product_id = :productId 
        AND is_active = true 
        AND attributes->>'color' IS NOT NULL
        AND stock > 0
        ORDER BY attributes->>'color' ASC
        """, nativeQuery = true)
    List<String> findAvailableColorsByProductId(@Param("productId") UUID productId);

    /**
     * Obtiene tallas disponibles para un producto y color específico
     */
    @Query(value = """
        SELECT DISTINCT attributes->>'size' as size
        FROM variants 
        WHERE product_id = :productId 
        AND is_active = true 
        AND attributes->>'size' IS NOT NULL
        AND stock > 0
        AND (:color IS NULL OR attributes->>'color' = :color)
        ORDER BY attributes->>'size' ASC
        """, nativeQuery = true)
    List<String> findAvailableSizesByProductIdAndColor(
        @Param("productId") UUID productId, 
        @Param("color") String color
    );


}