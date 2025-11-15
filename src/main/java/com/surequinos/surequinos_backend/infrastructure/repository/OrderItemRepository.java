package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para operaciones de items de orden con queries nativas
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * Obtiene todos los items de una orden
     */
    @Query(value = """
        SELECT * FROM order_items 
        WHERE order_id = :orderId 
        ORDER BY created_at ASC
        """, nativeQuery = true)
    List<OrderItem> findByOrderId(@Param("orderId") UUID orderId);

    /**
     * Obtiene todos los items de una variante específica
     */
    @Query(value = """
        SELECT * FROM order_items 
        WHERE variant_id = :variantId 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<OrderItem> findByVariantId(@Param("variantId") UUID variantId);

    /**
     * Cuenta cuántos items tiene una orden
     */
    @Query(value = "SELECT COUNT(*) FROM order_items WHERE order_id = :orderId", nativeQuery = true)
    Integer countByOrderId(@Param("orderId") UUID orderId);
}

