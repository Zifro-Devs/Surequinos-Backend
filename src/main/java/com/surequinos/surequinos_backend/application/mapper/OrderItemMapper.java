package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.OrderItemDto;
import com.surequinos.surequinos_backend.domain.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para conversiones entre OrderItem entity y DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemMapper {

    /**
     * Convierte una entidad OrderItem a OrderItemDto
     */
    @Mapping(target = "variantSku", ignore = true)
    @Mapping(target = "productName", ignore = true)
    OrderItemDto toDto(OrderItem orderItem);

    /**
     * Convierte una lista de entidades OrderItem a lista de OrderItemDto
     */
    List<OrderItemDto> toDtoList(List<OrderItem> orderItems);

    /**
     * Convierte OrderItemDto a entidad OrderItem (para actualizaciones)
     */
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "variant", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "variantId", ignore = true)
    OrderItem toEntity(OrderItemDto dto);
}

