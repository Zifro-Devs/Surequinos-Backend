package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.CategoryDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateCategoryRequest;
import com.surequinos.surequinos_backend.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para conversiones entre Category entity y DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    /**
     * Convierte una entidad Category a CategoryDto
     */
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    CategoryDto toDto(Category category);

    /**
     * Convierte una lista de entidades Category a lista de CategoryDto
     */
    List<CategoryDto> toDtoList(List<Category> categories);

    /**
     * Convierte un CreateCategoryRequest a entidad Category
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CreateCategoryRequest request);

    /**
     * Convierte CategoryDto a entidad Category (para actualizaciones)
     */
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryDto dto);
}