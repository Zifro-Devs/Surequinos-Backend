-- Ejecuta este script en tu base de datos PostgreSQL para crear la vista
-- Esta es la definición exacta de tu vista local

-- OPCIÓN 1: Si tus tablas están en el esquema 'railway'
-- Descomenta esta versión y comenta la OPCIÓN 2
/*
CREATE OR REPLACE VIEW railway.v_products_full AS
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
                'color', v.color,
                'size', v.size,
                'type', v.type,
                'price', v.price,
                'stock', v.stock,
                'imageUrl', v.image_url,
                'isActive', v.is_active,
                'available', v.stock > 0 AND v.is_active,
                'createdAt', v.created_at
            ) ORDER BY v.color, v.size, v.type
        ) FILTER (WHERE v.id IS NOT NULL),
        '[]'::json
    ) AS variants,
    COALESCE(MIN(v.price), p.base_price) AS min_price,
    COALESCE(MAX(v.price), p.base_price) AS max_price,
    COALESCE(SUM(v.stock), 0::bigint) AS total_stock,
    COALESCE(SUM(v.stock), 0::bigint) > 0 AS has_stock
FROM railway.products p
LEFT JOIN railway.categories c ON p.category_id = c.id
LEFT JOIN railway.variants v ON p.id = v.product_id AND v.is_active = true
WHERE p.is_active = true
GROUP BY p.id, p.name, p.slug, p.description, p.images, p.base_price, p.is_active, p.created_at, p.category_id, c.name, c.slug
ORDER BY p.created_at DESC;
*/

-- OPCIÓN 2: Si tus tablas están en el esquema 'public' (por defecto)
-- Esta es la versión estándar
CREATE OR REPLACE VIEW public.v_products_full AS
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
                'color', v.color,
                'size', v.size,
                'type', v.type,
                'price', v.price,
                'stock', v.stock,
                'imageUrl', v.image_url,
                'isActive', v.is_active,
                'available', v.stock > 0 AND v.is_active,
                'createdAt', v.created_at
            ) ORDER BY v.color, v.size, v.type
        ) FILTER (WHERE v.id IS NOT NULL),
        '[]'::json
    ) AS variants,
    COALESCE(MIN(v.price), p.base_price) AS min_price,
    COALESCE(MAX(v.price), p.base_price) AS max_price,
    COALESCE(SUM(v.stock), 0::bigint) AS total_stock,
    COALESCE(SUM(v.stock), 0::bigint) > 0 AS has_stock
FROM public.products p
LEFT JOIN public.categories c ON p.category_id = c.id
LEFT JOIN public.variants v ON p.id = v.product_id AND v.is_active = true
WHERE p.is_active = true
GROUP BY p.id, p.name, p.slug, p.description, p.images, p.base_price, p.is_active, p.created_at, p.category_id, c.name, c.slug
ORDER BY p.created_at DESC;
