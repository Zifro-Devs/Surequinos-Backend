package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.ProductDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateProductRequest;
import com.surequinos.surequinos_backend.domain.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para conversiones entre Product entity y DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {VariantMapper.class})
public interface ProductMapper {

    /**
     * Convierte una entidad Product a ProductDto
     */
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "categorySlug", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "minPrice", ignore = true)
    @Mapping(target = "maxPrice", ignore = true)
    @Mapping(target = "totalStock", ignore = true)
    @Mapping(target = "hasStock", ignore = true)
    ProductDto toDto(Product product);

    /**
     * Convierte una lista de entidades Product a lista de ProductDto
     */
    List<ProductDto> toDtoList(List<Product> products);

    /**
     * Convierte un CreateProductRequest a entidad Product
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    Product toEntity(CreateProductRequest request);

    /**
     * Convierte ProductDto a entidad Product (para actualizaciones)
     */
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    Product toEntity(ProductDto dto);
}