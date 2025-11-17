package com.surequinos.surequinos_backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Entidad que representa una variante de producto
 * Usa JSONB para atributos flexibles (color, talla, tipo, etc.)
 */
@Entity
@Table(name = "variants", indexes = {
    @Index(name = "idx_product", columnList = "product_id"),
    @Index(name = "idx_sku", columnList = "sku")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;

    /**
     * Atributos dinámicos en formato JSONB
     * Permite guardar cualquier combinación de atributos:
     * {
     *   "color": "Roble",
     *   "color_hex": "#C4622D",
     *   "size": "14\"",
     *   "type": "Americana",
     *   "material": "Cuero",
     *   ...
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock")
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relación con producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    // ========== MÉTODOS HELPER PARA COMPATIBILIDAD ==========

    /**
     * Obtener el color de la variante
     */
    public String getColor() {
        return getAttribute("color");
    }

    /**
     * Establecer el color de la variante
     */
    public void setColor(String color) {
        setAttribute("color", color);
    }

    /**
     * Obtener la talla de la variante
     */
    public String getSize() {
        return getAttribute("size");
    }

    /**
     * Establecer la talla de la variante
     */
    public void setSize(String size) {
        setAttribute("size", size);
    }



    /**
     * Obtener el código hexadecimal del color
     */
    public String getColorHex() {
        return getAttribute("color_hex");
    }

    /**
     * Establecer el código hexadecimal del color
     */
    public void setColorHex(String colorHex) {
        setAttribute("color_hex", colorHex);
    }

    /**
     * Obtener cualquier atributo por su clave
     */
    public String getAttribute(String key) {
        if (attributes == null) return null;
        Object value = attributes.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Establecer un atributo
     */
    public void setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        if (value != null) {
            attributes.put(key, value);
        } else {
            attributes.remove(key);
        }
    }

    /**
     * Verificar si tiene un atributo específico
     */
    public boolean hasAttribute(String key) {
        return attributes != null && attributes.containsKey(key);
    }

    /**
     * Obtener todos los atributos como Map
     */
    public Map<String, Object> getAllAttributes() {
        return attributes != null ? new HashMap<>(attributes) : new HashMap<>();
    }

    /**
     * Verifica si la variante está disponible
     */
    public boolean isAvailable() {
        return stock > 0 && isActive;
    }
}