package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para operaciones de direcciones
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    /**
     * Busca todas las direcciones de un usuario por su ID
     */
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(UUID userId);

    /**
     * Busca todas las direcciones de un usuario por su email
     */
    @Query(value = """
        SELECT a.* FROM addresses a
        INNER JOIN users u ON a.user_id = u.id
        WHERE u.email = :email AND u.status != 'DELETED'
        ORDER BY a.is_default DESC, a.created_at DESC
        """, nativeQuery = true)
    List<Address> findByUserEmail(@Param("email") String email);

    /**
     * Busca la dirección por defecto de un usuario
     */
    Address findByUserIdAndIsDefaultTrue(UUID userId);

    /**
     * Cuenta cuántas direcciones tiene un usuario
     */
    long countByUserId(UUID userId);
}

