package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.VariantDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateVariantRequest;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para conversiones entre Variant entity y DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VariantMapper {

    /**
     * Convierte una entidad Variant a VariantDto
     */
    @Mapping(target = "available", expression = "java(variant.isAvailable())")
    VariantDto toDto(Variant variant);

    /**
     * Convierte una lista de entidades Variant a lista de VariantDto
     */
    List<VariantDto> toDtoList(List<Variant> variants);

    /**
     * Convierte un CreateVariantRequest a entidad Variant
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "product", ignore = true)
    Variant toEntity(CreateVariantRequest request);

    /**
     * Convierte VariantDto a entidad Variant (para actualizaciones)
     */
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "productId", ignore = true)
    Variant toEntity(VariantDto dto);
}