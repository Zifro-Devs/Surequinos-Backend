package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.CartDto;
import com.surequinos.surequinos_backend.application.dto.CartItemDto;
import com.surequinos.surequinos_backend.domain.entity.Cart;
import com.surequinos.surequinos_backend.domain.entity.CartItem;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper para conversiones entre Cart/CartItem entities y DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {VariantMapper.class, ProductMapper.class})
public interface CartMapper {

    /**
     * Convierte una entidad Cart a CartDto
     */
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cart))")
    @Mapping(target = "itemCount", expression = "java(calculateItemCount(cart))")
    CartDto toDto(Cart cart);

    /**
     * Convierte una entidad CartItem a CartItemDto
     */
    @Mapping(target = "product", source = "variant.product")
    @Mapping(target = "subtotal", expression = "java(calculateItemSubtotal(cartItem))")
    CartItemDto toItemDto(CartItem cartItem);

    /**
     * Convierte una lista de CartItem a lista de CartItemDto
     */
    List<CartItemDto> toItemDtoList(List<CartItem> cartItems);

    /**
     * Calcula el subtotal del carrito
     */
    default BigDecimal calculateSubtotal(Cart cart) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.getCartItems().stream()
            .map(this::calculateItemSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el subtotal de un item
     */
    default BigDecimal calculateItemSubtotal(CartItem item) {
        if (item.getPrice() == null || item.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    /**
     * Calcula la cantidad total de items en el carrito
     */
    default Integer calculateItemCount(Cart cart) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return 0;
        }
        return cart.getCartItems().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    /**
     * Asegura que items nunca sea null
     */
    @AfterMapping
    default void ensureItemsNotNull(@MappingTarget CartDto cartDto) {
        if (cartDto.getItems() == null) {
            cartDto.setItems(new java.util.ArrayList<>());
        }
    }
}
