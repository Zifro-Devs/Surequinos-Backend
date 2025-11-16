package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de usuarios con queries nativas
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por su email (excluyendo usuarios eliminados)
     */
    @Query(value = "SELECT * FROM users WHERE email = :email AND status != 'DELETED'", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Busca un usuario por su email (incluyendo eliminados)
     */
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailIncludingDeleted(@Param("email") String email);

    /**
     * Busca un usuario por su número de documento (excluyendo usuarios eliminados)
     */
    @Query(value = "SELECT * FROM users WHERE document_number = :documentNumber AND status != 'DELETED'", nativeQuery = true)
    Optional<User> findByDocumentNumber(@Param("documentNumber") String documentNumber);

    /**
     * Busca un usuario por su número de documento (incluyendo eliminados)
     */
    @Query(value = "SELECT * FROM users WHERE document_number = :documentNumber", nativeQuery = true)
    Optional<User> findByDocumentNumberIncludingDeleted(@Param("documentNumber") String documentNumber);

    /**
     * Verifica si existe un usuario activo con el email dado
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE email = :email AND status != 'DELETED' AND (:excludeId IS NULL OR id != :excludeId)", nativeQuery = true)
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") UUID excludeId);

    /**
     * Verifica si existe un usuario activo con el número de documento dado
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE document_number = :documentNumber AND status != 'DELETED' AND (:excludeId IS NULL OR id != :excludeId)", nativeQuery = true)
    boolean existsByDocumentNumberAndIdNot(@Param("documentNumber") String documentNumber, @Param("excludeId") UUID excludeId);

    /**
     * Busca un usuario por su email y número de documento (excluyendo usuarios eliminados)
     */
    @Query(value = "SELECT * FROM users WHERE email = :email AND document_number = :documentNumber AND status != 'DELETED'", nativeQuery = true)
    Optional<User> findByEmailAndDocumentNumber(@Param("email") String email, @Param("documentNumber") String documentNumber);

    /**
     * Busca un usuario por su email y número de documento (incluyendo eliminados)
     */
    @Query(value = "SELECT * FROM users WHERE email = :email AND document_number = :documentNumber", nativeQuery = true)
    Optional<User> findByEmailAndDocumentNumberIncludingDeleted(@Param("email") String email, @Param("documentNumber") String documentNumber);

    /**
     * Búsqueda unificada de usuarios con todos los filtros posibles
     * Todos los parámetros son opcionales. Si se proporcionan, se aplican como filtros AND
     * - name, email, documentNumber, phoneNumber: búsqueda parcial (LIKE)
     * - roles: múltiples roles separados por comas (filtro OR - cualquiera de los roles)
     * - statuses: múltiples estados separados por comas (filtro OR - cualquiera de los estados)
     * - startDate, endDate: rango de fechas de creación
     */
    @Query(value = """
        SELECT DISTINCT u.* FROM users u
        LEFT JOIN roles r ON u.role_id = r.id
        WHERE 
            (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND
            (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND
            (:documentNumber IS NULL OR u.document_number LIKE CONCAT('%', :documentNumber, '%')) AND
            (:phoneNumber IS NULL OR u.phone_number LIKE CONCAT('%', :phoneNumber, '%')) AND
            (:roles IS NULL OR r.name::text = ANY(STRING_TO_ARRAY(:roles, ','))) AND
            (:statuses IS NULL OR u.status::text = ANY(STRING_TO_ARRAY(:statuses, ','))) AND
            (CASE WHEN :startDate IS NULL THEN TRUE ELSE u.created_at >= TO_TIMESTAMP(:startDate, 'YYYY-MM-DD HH24:MI:SS') END) AND
            (CASE WHEN :endDate IS NULL THEN TRUE ELSE u.created_at <= TO_TIMESTAMP(:endDate, 'YYYY-MM-DD HH24:MI:SS') END)
        ORDER BY u.created_at DESC
        """, nativeQuery = true)
    List<User> searchUsers(
        @Param("name") String name,
        @Param("email") String email,
        @Param("documentNumber") String documentNumber,
        @Param("phoneNumber") String phoneNumber,
        @Param("roles") String roles,
        @Param("statuses") String statuses,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
}


