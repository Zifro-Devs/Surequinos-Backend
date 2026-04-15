-- Agregar campos de descuento a la tabla variants
ALTER TABLE variants
ADD COLUMN IF NOT EXISTS discount_percentage DECIMAL(5,2),
ADD COLUMN IF NOT EXISTS discount_start_date TIMESTAMP,
ADD COLUMN IF NOT EXISTS discount_end_date TIMESTAMP;

-- Crear índice para búsquedas de variantes con descuento
CREATE INDEX IF NOT EXISTS idx_variants_discount ON variants(discount_percentage) WHERE discount_percentage IS NOT NULL;

-- Comentarios
COMMENT ON COLUMN variants.discount_percentage IS 'Porcentaje de descuento aplicado a la variante (0-99.99)';
COMMENT ON COLUMN variants.discount_start_date IS 'Fecha de inicio del descuento (opcional)';
COMMENT ON COLUMN variants.discount_end_date IS 'Fecha de fin del descuento (opcional)';
