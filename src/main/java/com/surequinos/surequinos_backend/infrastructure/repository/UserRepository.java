package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de usuarios con queries nativas
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por su email
     */
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Busca un usuario por su número de documento
     */
    @Query(value = "SELECT * FROM users WHERE document_number = :documentNumber", nativeQuery = true)
    Optional<User> findByDocumentNumber(@Param("documentNumber") String documentNumber);

    /**
     * Verifica si existe un usuario con el email dado
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE email = :email AND (:excludeId IS NULL OR id != :excludeId)", nativeQuery = true)
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") UUID excludeId);

    /**
     * Verifica si existe un usuario con el número de documento dado
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE document_number = :documentNumber AND (:excludeId IS NULL OR id != :excludeId)", nativeQuery = true)
    boolean existsByDocumentNumberAndIdNot(@Param("documentNumber") String documentNumber, @Param("excludeId") UUID excludeId);
}


