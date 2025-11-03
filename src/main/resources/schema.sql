-- ============================================
-- CREACIÓN DE VISTA v_products_full
-- ============================================

-- Eliminar vista si existe
DROP VIEW IF EXISTS v_products_full;

-- Crear vista con información completa de productos
CREATE VIEW v_products_full AS
SELECT 
    p.id,
    p.name,
    p.slug,
    p.description,
    p.images,
    p.base_price,
    c.name as category,
    c.slug as category_slug,
    COALESCE(
        json_agg(
            json_build_object(
                'id', v.id,
                'sku', v.sku,
                'color', v.color,
                'size', v.size,
                'type', v.type,
                'price', v.price,
                'stock', v.stock,
                'imageUrl', v.image_url,
                'available', (v.stock > 0 AND v.is_active)
            ) ORDER BY v.color, v.size, v.type
        ) FILTER (WHERE v.id IS NOT NULL),
        '[]'::json
    ) as variants
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN variants v ON p.id = v.product_id AND v.is_active = true
WHERE p.is_active = true
GROUP BY p.id, p.name, p.slug, p.description, p.images, p.base_price, c.name, c.slug;