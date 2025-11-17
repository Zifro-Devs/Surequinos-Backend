package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.CartDto;
import com.surequinos.surequinos_backend.application.dto.CartItemDto;
import com.surequinos.surequinos_backend.application.dto.request.AddToCartRequest;
import com.surequinos.surequinos_backend.application.mapper.CartMapper;
import com.surequinos.surequinos_backend.domain.entity.Cart;
import com.surequinos.surequinos_backend.domain.entity.CartItem;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import com.surequinos.surequinos_backend.infrastructure.repository.CartItemRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.CartRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio para gestión del carrito de compras
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final VariantRepository variantRepository;
    private final CartMapper cartMapper;

    private static final int CART_EXPIRATION_DAYS = 30;

    /**
     * Obtiene o crea un carrito basado en sessionId o userId
     */
    @Transactional
    public CartDto getOrCreateCart(String sessionId, UUID userId) {
        log.debug("Obteniendo o creando carrito - sessionId: {}, userId: {}", sessionId, userId);

        Cart cart;

        // Priorizar userId si está presente
        if (userId != null) {
            cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(sessionId, userId));
        } else if (sessionId != null) {
            cart = cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> createCart(sessionId, null));
        } else {
            throw new IllegalArgumentException("Se requiere sessionId o userId");
        }

        return cartMapper.toDto(cart);
    }

    /**
     * Agrega un item al carrito
     */
    @Transactional
    public CartItemDto addItemToCart(String sessionId, UUID userId, AddToCartRequest request) {
        log.info("Agregando item al carrito - variantId: {}, cantidad: {}", request.getVariantId(), request.getQuantity());

        // Validar que la variante existe y está disponible
        Variant variant = variantRepository.findById(request.getVariantId())
            .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada"));

        if (!variant.getIsActive()) {
            throw new IllegalArgumentException("La variante no está activa");
        }

        if (variant.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + variant.getStock());
        }

        // Obtener o crear carrito
        Cart cart = getOrCreateCartEntity(sessionId, userId);

        // Verificar si el item ya existe en el carrito
        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), request.getVariantId())
            .map(existing -> {
                // Actualizar cantidad
                int newQuantity = existing.getQuantity() + request.getQuantity();
                if (variant.getStock() < newQuantity) {
                    throw new IllegalArgumentException("Stock insuficiente. Disponible: " + variant.getStock());
                }
                existing.setQuantity(newQuantity);
                log.debug("Actualizando cantidad del item existente a: {}", newQuantity);
                return cartItemRepository.save(existing);
            })
            .orElseGet(() -> {
                // Crear nuevo item
                CartItem newItem = CartItem.builder()
                    .cartId(cart.getId())
                    .variantId(request.getVariantId())
                    .quantity(request.getQuantity())
                    .price(variant.getPrice())
                    .build();
                log.debug("Creando nuevo item en el carrito");
                return cartItemRepository.save(newItem);
            });

        return cartMapper.toItemDto(cartItem);
    }

    /**
     * Actualiza la cantidad de un item en el carrito
     */
    @Transactional
    public CartItemDto updateCartItem(String sessionId, UUID userId, UUID itemId, Integer quantity) {
        log.info("Actualizando item del carrito - itemId: {}, nueva cantidad: {}", itemId, quantity);

        Cart cart = getOrCreateCartEntity(sessionId, userId);

        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item no encontrado en el carrito"));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new IllegalArgumentException("El item no pertenece a este carrito");
        }

        // Validar stock
        Variant variant = variantRepository.findById(cartItem.getVariantId())
            .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada"));

        if (variant.getStock() < quantity) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + variant.getStock());
        }

        cartItem.setQuantity(quantity);
        CartItem updated = cartItemRepository.save(cartItem);

        return cartMapper.toItemDto(updated);
    }

    /**
     * Elimina un item del carrito
     */
    @Transactional
    public void removeCartItem(String sessionId, UUID userId, UUID itemId) {
        log.info("Eliminando item del carrito - itemId: {}", itemId);

        Cart cart = getOrCreateCartEntity(sessionId, userId);

        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item no encontrado en el carrito"));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new IllegalArgumentException("El item no pertenece a este carrito");
        }

        cartItemRepository.delete(cartItem);
        log.debug("Item eliminado del carrito");
    }

    /**
     * Vacía el carrito
     */
    @Transactional
    public void clearCart(String sessionId, UUID userId) {
        log.info("Vaciando carrito - sessionId: {}, userId: {}", sessionId, userId);

        Cart cart = getOrCreateCartEntity(sessionId, userId);
        cartItemRepository.deleteByCartId(cart.getId());

        log.debug("Carrito vaciado");
    }

    /**
     * Obtiene el carrito actual
     */
    @Transactional(readOnly = true)
    public CartDto getCurrentCart(String sessionId, UUID userId) {
        log.debug("Obteniendo carrito actual - sessionId: {}, userId: {}", sessionId, userId);

        Cart cart = getOrCreateCartEntity(sessionId, userId);
        return cartMapper.toDto(cart);
    }

    /**
     * Método auxiliar para obtener o crear la entidad Cart
     */
    private Cart getOrCreateCartEntity(String sessionId, UUID userId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(sessionId, userId));
        } else if (sessionId != null) {
            return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> createCart(sessionId, null));
        } else {
            throw new IllegalArgumentException("Se requiere sessionId o userId");
        }
    }

    /**
     * Crea un nuevo carrito
     */
    private Cart createCart(String sessionId, UUID userId) {
        Cart cart = Cart.builder()
            .sessionId(sessionId)
            .userId(userId)
            .expiresAt(LocalDateTime.now().plusDays(CART_EXPIRATION_DAYS))
            .build();

        Cart saved = cartRepository.save(cart);
        log.info("Carrito creado - ID: {}, sessionId: {}, userId: {}", saved.getId(), sessionId, userId);
        return saved;
    }

    /**
     * Limpia carritos expirados (para ejecutar periódicamente)
     */
    @Transactional
    public int cleanExpiredCarts() {
        log.info("Limpiando carritos expirados");
        int deleted = cartRepository.deleteExpiredCarts(LocalDateTime.now());
        log.info("Carritos expirados eliminados: {}", deleted);
        return deleted;
    }
}
