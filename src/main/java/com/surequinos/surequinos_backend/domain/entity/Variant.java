package com.surequinos.surequinos_backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa una variante de producto
 * Define color, talla, tipo, precio y stock específicos
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

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "size", length = 20)
    private String size;

    @Column(name = "type", length = 50)
    private String type;

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

    /**
     * Verifica si la variante está disponible
     */
    public boolean isAvailable() {
        return stock > 0 && isActive;
    }
}