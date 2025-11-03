package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.AttributeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para operaciones de opciones de atributos
 */
@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, UUID> {

    /**
     * Obtiene todas las opciones de un atributo específico ordenadas por display_order
     */
    @Query(value = """
        SELECT * FROM attribute_options 
        WHERE attribute_name = :attributeName 
        ORDER BY display_order ASC, value ASC
        """, nativeQuery = true)
    List<AttributeOption> findByAttributeNameOrderByDisplayOrder(@Param("attributeName") String attributeName);

    /**
     * Obtiene todos los nombres de atributos únicos
     */
    @Query(value = """
        SELECT DISTINCT attribute_name 
        FROM attribute_options 
        ORDER BY attribute_name ASC
        """, nativeQuery = true)
    List<String> findDistinctAttributeNames();

    /**
     * Verifica si existe una combinación atributo-valor
     */
    @Query(value = """
        SELECT COUNT(*) > 0 FROM attribute_options 
        WHERE attribute_name = :attributeName AND value = :value
        """, nativeQuery = true)
    boolean existsByAttributeNameAndValue(@Param("attributeName") String attributeName, @Param("value") String value);
}