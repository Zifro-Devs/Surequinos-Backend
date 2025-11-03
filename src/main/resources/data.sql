-- ============================================
-- SCRIPT DE INICIALIZACIÓN DE DATOS DE PRUEBA
-- Surequinos Ecommerce Database
-- ============================================

-- Limpiar datos existentes (opcional, comentar en producción)
-- DELETE FROM variants;
-- DELETE FROM products;
-- DELETE FROM categories;
-- DELETE FROM attribute_options;

-- ============================================
-- INSERTAR CATEGORÍAS
-- ============================================

-- Categorías principales
INSERT INTO categories (id, name, slug, display_order) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Línea de Pista', 'linea-de-pista', 1),
('550e8400-e29b-41d4-a716-446655440002', 'Sillas y Tereques', 'sillas-y-tereques', 2),
('550e8400-e29b-41d4-a716-446655440003', 'Aperos y Jaquimones', 'aperos-y-jaquimones', 3),
('550e8400-e29b-41d4-a716-446655440004', 'Accesorios', 'accesorios', 4)
ON CONFLICT (slug) DO NOTHING;

-- Subcategorías de Sillas y Tereques
INSERT INTO categories (id, name, slug, parent_id, display_order) VALUES
('550e8400-e29b-41d4-a716-446655440011', 'Sillas de Montar', 'sillas-de-montar', '550e8400-e29b-41d4-a716-446655440002', 1),
('550e8400-e29b-41d4-a716-446655440012', 'Tereques de Montar', 'tereques-de-montar', '550e8400-e29b-41d4-a716-446655440002', 2)
ON CONFLICT (slug) DO NOTHING;

-- Subcategorías de Accesorios
INSERT INTO categories (id, name, slug, parent_id, display_order) VALUES
('550e8400-e29b-41d4-a716-446655440021', 'Alfombras', 'alfombras', '550e8400-e29b-41d4-a716-446655440004', 1),
('550e8400-e29b-41d4-a716-446655440022', 'Riendas', 'riendas', '550e8400-e29b-41d4-a716-446655440004', 2),
('550e8400-e29b-41d4-a716-446655440023', 'Cinchas', 'cinchas', '550e8400-e29b-41d4-a716-446655440004', 3),
('550e8400-e29b-41d4-a716-446655440024', 'Tarabas', 'tarabas', '550e8400-e29b-41d4-a716-446655440004', 4),
('550e8400-e29b-41d4-a716-446655440025', 'Sombreros', 'sombreros', '550e8400-e29b-41d4-a716-446655440004', 5)
ON CONFLICT (slug) DO NOTHING;

-- ============================================
-- INSERTAR OPCIONES DE ATRIBUTOS
-- ============================================

-- Colores
INSERT INTO attribute_options (attribute_name, value, display_order) VALUES
('color', 'Roble', 1),
('color', 'Chocolate', 2),
('color', 'Negro', 3),
('color', 'Blanco', 4),
('color', 'Rojo', 5),
('color', 'Azul', 6),
('color', 'Verde', 7),
('color', 'Naranja', 8),
('color', 'Fucsia', 9),
('color', 'Dorada', 10),
('color', 'Café', 11),
('color', 'Gris', 12),
('color', 'Amarilla', 13)
ON CONFLICT (attribute_name, value) DO NOTHING;

-- Tallas sillas (con medios)
INSERT INTO attribute_options (attribute_name, value, display_order) VALUES
('size', '12"', 1),
('size', '12.5"', 2),
('size', '13"', 3),
('size', '13.5"', 4),
('size', '14"', 5),
('size', '14.5"', 6),
('size', '15"', 7),
('size', '15.5"', 8),
('size', '16"', 9),
('size', '17"', 10)
ON CONFLICT (attribute_name, value) DO NOTHING;

-- Tallas sombreros
INSERT INTO attribute_options (attribute_name, value, display_order) VALUES
('size', '53', 20),
('size', '54', 21),
('size', '55', 22),
('size', '56', 23),
('size', '57', 24),
('size', '58', 25),
('size', '59', 26),
('size', '60', 27)
ON CONFLICT (attribute_name, value) DO NOTHING;

-- Tipos
INSERT INTO attribute_options (attribute_name, value, display_order) VALUES
('type', 'Americana', 1),
('type', 'Trenzada', 2),
('type', 'Nacional', 3),
('type', 'Sencillo', 4),
('type', 'Peluche', 5),
('type', 'De Lujo', 6),
('type', 'Timbiano', 7),
('type', 'Tipo Timbiano', 8),
('type', 'Plano', 9),
('type', 'Lumillado', 10),
('type', 'Combinado', 11),
('type', 'De Nudos en Hilo', 12)
ON CONFLICT (attribute_name, value) DO NOTHING;

-- ============================================
-- INSERTAR PRODUCTOS DE PRUEBA
-- ============================================

-- 1. SILLA NIÑO
INSERT INTO products (id, category_id, name, slug, description, images, base_price) VALUES
('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440011', 'Silla Niño', 'silla-nino', 
'Silla de montar para niños de 1 a 12 años. Talla única 12"', 
ARRAY['silla-nino-1.jpg', 'silla-nino-2.jpg'], 650000.00)
ON CONFLICT (slug) DO NOTHING;

-- 2. SILLA CAUCANA
INSERT INTO products (id, category_id, name, slug, description, images, base_price) VALUES
('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440011', 'Silla Caucana', 'silla-caucana',
'Silla de montar tipo Caucana con alforja incluida. Disponible de 12" a 17" (tallas 16" y 17" por encargo)',
ARRAY['silla-caucana-1.jpg', 'silla-caucana-2.jpg', 'silla-caucana-3.jpg'], 850000.00)
ON CONFLICT (slug) DO NOTHING;

-- 3. TEREQUE WILSON
INSERT INTO products (id, category_id, name, slug, description, images, base_price) VALUES
('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440012', 'Tereque Wilson', 'tereque-wilson',
'Tereque de montar disponible en 12" a 17"', ARRAY['tereque-wilson-1.jpg'], 450000.00)
ON CONFLICT (slug) DO NOTHING;

-- 4. RIENDAS AMERICANA
INSERT INTO products (id, category_id, name, slug, description, images, base_price) VALUES
('650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440022', 'Riendas Americana', 'riendas-americana',
'Riendas tipo americana disponibles en blanco y negro', ARRAY['riendas-americana.jpg'], 65000.00)
ON CONFLICT (slug) DO NOTHING;

-- 5. RIENDAS TRENZADA
INSERT INTO products (id, category_id, name, slug, description, images, base_price) VALUES
('650e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440022', 'Riendas Trenzada', 'riendas-trenzada',
'Riendas trenzadas disponibles en blanco y negro', ARRAY['riendas-trenzada.jpg'], 75000.00)
ON CONFLICT (slug) DO NOTHING;

-- ============================================
-- INSERTAR VARIANTES DE PRUEBA
-- ============================================

-- Variantes para Silla Niño
INSERT INTO variants (id, product_id, sku, color, size, price, stock) VALUES
('750e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', 'SIL-NINO-ROBLE-12', 'Roble', '12"', 650000.00, 5),
('750e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440001', 'SIL-NINO-CHOC-12', 'Chocolate', '12"', 650000.00, 3),
('750e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440001', 'SIL-NINO-NEG-12', 'Negro', '12"', 650000.00, 4)
ON CONFLICT (sku) DO NOTHING;

-- Variantes para Silla Caucana (algunas variantes de ejemplo)
INSERT INTO variants (id, product_id, sku, color, size, price, stock) VALUES
('750e8400-e29b-41d4-a716-446655440011', '650e8400-e29b-41d4-a716-446655440002', 'SIL-CAU-ROBLE-14', 'Roble', '14"', 850000.00, 8),
('750e8400-e29b-41d4-a716-446655440012', '650e8400-e29b-41d4-a716-446655440002', 'SIL-CAU-ROBLE-15', 'Roble', '15"', 850000.00, 4),
('750e8400-e29b-41d4-a716-446655440013', '650e8400-e29b-41d4-a716-446655440002', 'SIL-CAU-CHOC-14', 'Chocolate', '14"', 850000.00, 7),
('750e8400-e29b-41d4-a716-446655440014', '650e8400-e29b-41d4-a716-446655440002', 'SIL-CAU-CHOC-15', 'Chocolate', '15"', 850000.00, 3),
('750e8400-e29b-41d4-a716-446655440015', '650e8400-e29b-41d4-a716-446655440002', 'SIL-CAU-NEG-14', 'Negro', '14"', 850000.00, 9),
('750e8400-e29b-41d4-a716-446655440016', '650e8400-e29b-41d4-a716-446655440002', 'SIL-CAU-NEG-15', 'Negro', '15"', 850000.00, 2)
ON CONFLICT (sku) DO NOTHING;

-- Variantes para Tereque Wilson
INSERT INTO variants (id, product_id, sku, color, size, price, stock) VALUES
('750e8400-e29b-41d4-a716-446655440021', '650e8400-e29b-41d4-a716-446655440003', 'TER-WILS-ROBLE-14', 'Roble', '14"', 450000.00, 6),
('750e8400-e29b-41d4-a716-446655440022', '650e8400-e29b-41d4-a716-446655440003', 'TER-WILS-ROBLE-15', 'Roble', '15"', 450000.00, 8),
('750e8400-e29b-41d4-a716-446655440023', '650e8400-e29b-41d4-a716-446655440003', 'TER-WILS-CHOC-14', 'Chocolate', '14"', 450000.00, 5),
('750e8400-e29b-41d4-a716-446655440024', '650e8400-e29b-41d4-a716-446655440003', 'TER-WILS-NEG-15', 'Negro', '15"', 450000.00, 6)
ON CONFLICT (sku) DO NOTHING;

-- Variantes para Riendas Americana
INSERT INTO variants (id, product_id, sku, color, type, price, stock) VALUES
('750e8400-e29b-41d4-a716-446655440031', '650e8400-e29b-41d4-a716-446655440004', 'RIEN-AMER-BLA', 'Blanco', 'Americana', 65000.00, 15),
('750e8400-e29b-41d4-a716-446655440032', '650e8400-e29b-41d4-a716-446655440004', 'RIEN-AMER-NEG', 'Negro', 'Americana', 65000.00, 12)
ON CONFLICT (sku) DO NOTHING;

-- Variantes para Riendas Trenzada
INSERT INTO variants (id, product_id, sku, color, type, price, stock) VALUES
('750e8400-e29b-41d4-a716-446655440041', '650e8400-e29b-41d4-a716-446655440005', 'RIEN-TRENZ-BLA', 'Blanco', 'Trenzada', 75000.00, 10),
('750e8400-e29b-41d4-a716-446655440042', '650e8400-e29b-41d4-a716-446655440005', 'RIEN-TRENZ-NEG', 'Negro', 'Trenzada', 75000.00, 8)
ON CONFLICT (sku) DO NOTHING;