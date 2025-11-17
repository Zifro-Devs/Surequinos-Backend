-- ============================================
-- CREACIÓN DE VISTA v_products_full
-- ============================================

-- Eliminar vista si existe
DROP VIEW IF EXISTS v_products_full;

-- Crear vista con información completa de productos
-- ACTUALIZADA para usar el campo attributes (JSONB)
CREATE VIEW v_products_full AS
SELECT 
    p.id,
    p.name,
    p.slug,
    p.description,
    p.images,
    p.base_price,
    p.is_active,
    p.created_at,
    p.category_id,
    c.name AS category,
    c.slug AS category_slug,
    COALESCE(
        json_agg(
            json_build_object(
                'id', v.id,
                'sku', v.sku,
                'attributes', v.attributes,
                'color', v.attributes->>'color',
                'size', v.attributes->>'size',
                'type', v.attributes->>'type',
                'price', v.price,
                'stock', v.stock,
                'imageUrl', v.image_url,
                'isActive', v.is_active,
                'available', v.stock > 0 AND v.is_active,
                'createdAt', v.created_at
            ) ORDER BY v.attributes->>'color', v.attributes->>'size', v.attributes->>'type'
        ) FILTER (WHERE v.id IS NOT NULL),
        '[]'::json
    ) AS variants,
    COALESCE(MIN(v.price), p.base_price) AS min_price,
    COALESCE(MAX(v.price), p.base_price) AS max_price,
    COALESCE(SUM(v.stock), 0::bigint) AS total_stock,
    COALESCE(SUM(v.stock), 0::bigint) > 0 AS has_stock
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN variants v ON p.id = v.product_id AND v.is_active = true
WHERE p.is_active = true
GROUP BY p.id, p.name, p.slug, p.description, p.images, p.base_price, p.is_active, p.created_at, p.category_id, c.name, c.slug
ORDER BY p.created_at DESC;