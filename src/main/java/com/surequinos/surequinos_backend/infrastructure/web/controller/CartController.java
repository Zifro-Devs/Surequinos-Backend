package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.CartDto;
import com.surequinos.surequinos_backend.application.dto.CartItemDto;
import com.surequinos.surequinos_backend.application.dto.request.AddToCartRequest;
import com.surequinos.surequinos_backend.application.dto.request.UpdateCartItemRequest;
import com.surequinos.surequinos_backend.application.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

/**
 * Controlador REST para gestión del carrito de compras
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Carrito", description = "API para gestión del carrito de compras")
public class CartController {

    private final CartService cartService;
    private static final String SESSION_COOKIE_NAME = "cart_session_id";
    private static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60; // 30 días

    @Operation(summary = "Obtener carrito actual",
               description = "Retorna el carrito actual del usuario o sesión")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<CartDto> getCart(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "ID del usuario autenticado (opcional)")
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        
        String sessionId = getOrCreateSessionId(request, response);
        log.info("GET /cart - sessionId: {}, userId: {}", sessionId, userId);

        CartDto cart = cartService.getCurrentCart(sessionId, userId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Agregar item al carrito",
               description = "Agrega un producto (variante) al carrito o incrementa su cantidad si ya existe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
        @ApiResponse(responseCode = "404", description = "Variante no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/items")
    public ResponseEntity<CartItemDto> addItem(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "ID del usuario autenticado (opcional)")
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @Parameter(description = "Datos del item a agregar")
            @Valid @RequestBody AddToCartRequest addRequest) {
        
        String sessionId = getOrCreateSessionId(request, response);
        log.info("POST /cart/items - sessionId: {}, userId: {}, variantId: {}", 
                sessionId, userId, addRequest.getVariantId());

        try {
            CartItemDto item = cartService.addItemToCart(sessionId, userId, addRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (IllegalArgumentException e) {
            log.warn("Error agregando item al carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar cantidad de item",
               description = "Actualiza la cantidad de un item específico en el carrito")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItemDto> updateItem(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "ID del usuario autenticado (opcional)")
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @Parameter(description = "ID del item en el carrito")
            @PathVariable UUID itemId,
            @Parameter(description = "Nueva cantidad")
            @Valid @RequestBody UpdateCartItemRequest updateRequest) {
        
        String sessionId = getOrCreateSessionId(request, response);
        log.info("PUT /cart/items/{} - sessionId: {}, userId: {}, cantidad: {}", 
                itemId, sessionId, userId, updateRequest.getQuantity());

        try {
            CartItemDto item = cartService.updateCartItem(sessionId, userId, itemId, updateRequest.getQuantity());
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando item del carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar item del carrito",
               description = "Elimina un item específico del carrito")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Item eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "ID del usuario autenticado (opcional)")
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @Parameter(description = "ID del item en el carrito")
            @PathVariable UUID itemId) {
        
        String sessionId = getOrCreateSessionId(request, response);
        log.info("DELETE /cart/items/{} - sessionId: {}, userId: {}", itemId, sessionId, userId);

        try {
            cartService.removeCartItem(sessionId, userId, itemId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error eliminando item del carrito: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Vaciar carrito",
               description = "Elimina todos los items del carrito")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "ID del usuario autenticado (opcional)")
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        
        String sessionId = getOrCreateSessionId(request, response);
        log.info("DELETE /cart - sessionId: {}, userId: {}", sessionId, userId);

        cartService.clearCart(sessionId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene o crea un session ID desde las cookies
     */
    private String getOrCreateSessionId(HttpServletRequest request, HttpServletResponse response) {
        // Buscar cookie existente
        if (request.getCookies() != null) {
            String existingSessionId = Arrays.stream(request.getCookies())
                .filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

            if (existingSessionId != null && !existingSessionId.isEmpty()) {
                return existingSessionId;
            }
        }

        // Crear nuevo session ID
        String newSessionId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, newSessionId);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // Descomentar en producción con HTTPS
        response.addCookie(cookie);

        log.debug("Nuevo session ID creado: {}", newSessionId);
        return newSessionId;
    }
}
