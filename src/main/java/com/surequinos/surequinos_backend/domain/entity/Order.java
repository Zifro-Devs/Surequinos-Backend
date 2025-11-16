package com.surequinos.surequinos_backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa una orden de compra
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_payment_status", columnList = "payment_status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "discount_value", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountValue = BigDecimal.ZERO;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "payment_status", nullable = false, length = 50)
    @Builder.Default
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED, REFUNDED

    @Column(name = "payment_method", length = 100)
    private String paymentMethod; // Ej: "TARJETA_CREDITO", "TARJETA_DEBITO", "EFECTIVO", "TRANSFERENCIA", etc.

    @Column(name = "shipping_value", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingValue = BigDecimal.ZERO;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "PENDING"; // PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con usuario/cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // Relación con items de la orden
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}

