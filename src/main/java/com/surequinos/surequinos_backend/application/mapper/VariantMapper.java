package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.VariantDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateVariantRequest;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para conversiones entre Variant entity y DTOs
 * Maneja la conversión de atributos JSONB
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VariantMapper {

    /**
     * Convierte una entidad Variant a VariantDto
     * Extrae color y size de attributes para compatibilidad
     */
    @Mapping(target = "available", expression = "java(variant.isAvailable())")
    @Mapping(target = "color", expression = "java(variant.getColor())")
    @Mapping(target = "size", expression = "java(variant.getSize())")
    VariantDto toDto(Variant variant);

    /**
     * Convierte una lista de entidades Variant a lista de VariantDto
     */
    List<VariantDto> toDtoList(List<Variant> variants);

    /**
     * Convierte un CreateVariantRequest a entidad Variant
     * Coloca color, size, type en el campo attributes
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    Variant toEntity(CreateVariantRequest request);

    /**
     * Después de mapear CreateVariantRequest, poblar attributes
     */
    @AfterMapping
    default void populateAttributes(@MappingTarget Variant variant, CreateVariantRequest request) {
        if (request.getColor() != null) {
            variant.setColor(request.getColor());
        }
        if (request.getSize() != null) {
            variant.setSize(request.getSize());
        }
    }

    /**
     * Convierte VariantDto a entidad Variant (para actualizaciones)
     */
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    Variant toEntity(VariantDto dto);

    /**
     * Después de mapear VariantDto, poblar attributes
     */
    @AfterMapping
    default void populateAttributesFromDto(@MappingTarget Variant variant, VariantDto dto) {
        if (dto.getColor() != null) {
            variant.setColor(dto.getColor());
        }
        if (dto.getSize() != null) {
            variant.setSize(dto.getSize());
        }
        // Si el DTO tiene attributes completo, usarlo
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            variant.setAttributes(dto.getAttributes());
        }
    }
}