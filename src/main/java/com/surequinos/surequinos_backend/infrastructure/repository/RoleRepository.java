package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.Role;
import com.surequinos.surequinos_backend.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de roles con queries nativas
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Busca un rol por su nombre (enum)
     */
    @Query(value = "SELECT * FROM roles WHERE name = :name", nativeQuery = true)
    Optional<Role> findByName(@Param("name") String name);

    /**
     * Busca un rol por su enum UserRole
     */
    default Optional<Role> findByUserRole(UserRole userRole) {
        return findByName(userRole.name());
    }
}

