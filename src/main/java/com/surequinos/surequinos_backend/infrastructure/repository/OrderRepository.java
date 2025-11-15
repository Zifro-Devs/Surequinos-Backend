package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de órdenes con queries nativas
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Busca una orden por su número de orden
     */
    @Query(value = "SELECT * FROM orders WHERE order_number = :orderNumber", nativeQuery = true)
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * Obtiene todas las órdenes de un usuario
     */
    @Query(value = """
        SELECT * FROM orders 
        WHERE user_id = :userId 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Order> findByUserId(@Param("userId") UUID userId);

    /**
     * Obtiene órdenes de un usuario con paginación
     */
    @Query(value = """
        SELECT * FROM orders 
        WHERE user_id = :userId 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    Page<Order> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Obtiene órdenes por estado
     */
    @Query(value = """
        SELECT * FROM orders 
        WHERE status = :status 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Order> findByStatus(@Param("status") String status);

    /**
     * Obtiene órdenes por estado de pago
     */
    @Query(value = """
        SELECT * FROM orders 
        WHERE payment_status = :paymentStatus 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Order> findByPaymentStatus(@Param("paymentStatus") String paymentStatus);

    /**
     * Obtiene todas las órdenes ordenadas por fecha de creación
     */
    @Query(value = "SELECT * FROM orders ORDER BY created_at DESC", nativeQuery = true)
    Page<Order> findAllOrderedByCreatedAt(Pageable pageable);

    /**
     * Busca órdenes por número de orden (búsqueda parcial)
     */
    @Query(value = """
        SELECT * FROM orders 
        WHERE order_number LIKE CONCAT('%', :searchText, '%') 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Order> searchByOrderNumber(@Param("searchText") String searchText);

    /**
     * Obtiene órdenes por rango de fechas
     */
    @Query(value = """
        SELECT * FROM orders 
        WHERE created_at >= :startDate AND created_at <= :endDate 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Order> findByDateRange(@Param("startDate") java.time.LocalDateTime startDate, 
                                @Param("endDate") java.time.LocalDateTime endDate);
}

