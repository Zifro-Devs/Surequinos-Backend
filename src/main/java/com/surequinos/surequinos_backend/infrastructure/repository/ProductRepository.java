package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de productos con queries nativas
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Busca un producto por su slug
     */
    @Query(value = "SELECT * FROM products WHERE slug = :slug AND is_active = true", nativeQuery = true)
    Optional<Product> findBySlug(@Param("slug") String slug);

    /**
     * Obtiene productos activos con paginación
     */
    @Query(value = """
        SELECT * FROM products 
        WHERE is_active = true 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    Page<Product> findActiveProducts(Pageable pageable);

    /**
     * Busca productos por categoría
     */
    @Query(value = """
        SELECT * FROM products 
        WHERE category_id = :categoryId AND is_active = true 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    Page<Product> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    /**
     * Busca productos por texto (nombre o descripción)
     */
    @Query(value = """
        SELECT * FROM products 
        WHERE is_active = true 
        AND (LOWER(name) LIKE LOWER(CONCAT('%', :searchText, '%')) 
             OR LOWER(description) LIKE LOWER(CONCAT('%', :searchText, '%')))
        ORDER BY 
            CASE WHEN LOWER(name) LIKE LOWER(CONCAT('%', :searchText, '%')) THEN 1 ELSE 2 END,
            created_at DESC
        """, nativeQuery = true)
    Page<Product> findBySearchText(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Obtiene productos con información completa usando la vista v_products_full
     */
    @Query(value = """
        SELECT 
            id,
            name,
            slug,
            description,
            images,
            base_price,
            is_active,
            created_at,
            category_id,
            category,
            category_slug,
            variants,
            min_price,
            max_price,
            total_stock,
            has_stock
        FROM v_products_full 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Object[]> findProductsFullView();

    /**
     * Obtiene un producto específico con toda su información
     */
    @Query(value = """
        SELECT 
            id,
            name,
            slug,
            description,
            images,
            base_price,
            is_active,
            created_at,
            category_id,
            category,
            category_slug,
            variants,
            min_price,
            max_price,
            total_stock,
            has_stock
        FROM v_products_full 
        WHERE slug = :slug
        """, nativeQuery = true)
    Optional<Object[]> findProductFullBySlug(@Param("slug") String slug);

    /**
     * Obtiene productos por categoría usando la vista
     */
    @Query(value = """
        SELECT 
            id,
            name,
            slug,
            description,
            images,
            base_price,
            is_active,
            created_at,
            category_id,
            category,
            category_slug,
            variants,
            min_price,
            max_price,
            total_stock,
            has_stock
        FROM v_products_full 
        WHERE category_slug = :categorySlug
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Object[]> findProductsByCategorySlug(@Param("categorySlug") String categorySlug);

    /**
     * Verifica si existe un producto con el slug dado (excluyendo un ID específico)
     */
    @Query(value = """
        SELECT COUNT(*) > 0 FROM products 
        WHERE slug = :slug AND (:excludeId IS NULL OR id != :excludeId)
        """, nativeQuery = true)
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("excludeId") UUID excludeId);

    /**
     * Obtiene productos con stock bajo
     */
    @Query(value = """
        SELECT DISTINCT p.* 
        FROM products p
        INNER JOIN variants v ON p.id = v.product_id
        WHERE p.is_active = true 
        AND v.is_active = true 
        AND v.stock < :threshold
        ORDER BY p.name ASC
        """, nativeQuery = true)
    List<Product> findProductsWithLowStock(@Param("threshold") Integer threshold);

    /**
     * Obtiene estadísticas de productos por categoría
     */
    @Query(value = """
        SELECT 
            c.name as category_name,
            c.slug as category_slug,
            COUNT(p.id) as product_count,
            COALESCE(SUM(v.stock), 0) as total_stock,
            COALESCE(AVG(v.price), 0) as avg_price
        FROM categories c
        LEFT JOIN products p ON c.id = p.category_id AND p.is_active = true
        LEFT JOIN variants v ON p.id = v.product_id AND v.is_active = true
        GROUP BY c.id, c.name, c.slug
        ORDER BY product_count DESC
        """, nativeQuery = true)
    List<Object[]> getProductStatsByCategory();
}