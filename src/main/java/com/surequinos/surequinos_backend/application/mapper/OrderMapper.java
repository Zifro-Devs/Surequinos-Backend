package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.OrderDto;
import com.surequinos.surequinos_backend.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para conversiones entre Order entity y DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderItemMapper.class})
public interface OrderMapper {

    /**
     * Convierte una entidad Order a OrderDto
     */
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "orderItems", source = "orderItems")
    OrderDto toDto(Order order);

    /**
     * Convierte una lista de entidades Order a lista de OrderDto
     */
    List<OrderDto> toDtoList(List<Order> orders);

    /**
     * Convierte OrderDto a entidad Order (para actualizaciones)
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Order toEntity(OrderDto dto);
}

