package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de categorías con queries nativas
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Busca una categoría por su slug
     */
    @Query(value = "SELECT * FROM categories WHERE slug = :slug", nativeQuery = true)
    Optional<Category> findBySlug(@Param("slug") String slug);

    /**
     * Obtiene todas las categorías principales (sin padre) ordenadas por display_order
     */
    @Query(value = """
        SELECT * FROM categories 
        WHERE parent_id IS NULL 
        ORDER BY display_order ASC, name ASC
        """, nativeQuery = true)
    List<Category> findMainCategories();

    /**
     * Obtiene las subcategorías de una categoría padre
     */
    @Query(value = """
        SELECT * FROM categories 
        WHERE parent_id = :parentId 
        ORDER BY display_order ASC, name ASC
        """, nativeQuery = true)
    List<Category> findSubcategories(@Param("parentId") UUID parentId);

    /**
     * Obtiene categorías con conteo de productos
     */
    @Query(value = """
        SELECT c.*, 
               COALESCE(COUNT(p.id), 0) as product_count
        FROM categories c
        LEFT JOIN products p ON c.id = p.category_id AND p.is_active = true
        WHERE c.parent_id IS NULL
        GROUP BY c.id, c.name, c.slug, c.display_order, c.created_at
        ORDER BY c.display_order ASC, c.name ASC
        """, nativeQuery = true)
    List<Object[]> findMainCategoriesWithProductCount();

    /**
     * Verifica si existe una categoría con el slug dado (excluyendo un ID específico)
     */
    @Query(value = """
        SELECT COUNT(*) > 0 FROM categories 
        WHERE slug = :slug AND (:excludeId IS NULL OR id != :excludeId)
        """, nativeQuery = true)
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("excludeId") UUID excludeId);

    /**
     * Obtiene la jerarquía completa de categorías
     */
    @Query(value = """
        WITH RECURSIVE category_hierarchy AS (
            -- Categorías principales
            SELECT id, parent_id, name, slug, display_order, 0 as level
            FROM categories 
            WHERE parent_id IS NULL
            
            UNION ALL
            
            -- Subcategorías recursivamente
            SELECT c.id, c.parent_id, c.name, c.slug, c.display_order, ch.level + 1
            FROM categories c
            INNER JOIN category_hierarchy ch ON c.parent_id = ch.id
        )
        SELECT * FROM category_hierarchy 
        ORDER BY level, display_order, name
        """, nativeQuery = true)
    List<Object[]> findCategoryHierarchy();
}