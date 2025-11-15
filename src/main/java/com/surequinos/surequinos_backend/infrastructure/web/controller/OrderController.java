package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.OrderDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateOrderRequest;
import com.surequinos.surequinos_backend.application.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestión de órdenes
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Órdenes", description = "API para gestión de órdenes de compra")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Crear nueva orden", 
               description = "Crea una nueva orden de compra. Valida stock, calcula totales y reduce inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @Parameter(description = "Datos de la nueva orden")
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /orders - Creando nueva orden para usuario: {}", request.getUserId());
        
        try {
            OrderDto createdOrder = orderService.createOrder(request);
            
            log.info("Orden creada exitosamente: {} (ID: {})", 
                    createdOrder.getOrderNumber(), createdOrder.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creando orden: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener orden por ID", 
               description = "Busca una orden específica por su ID único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(
            @Parameter(description = "ID único de la orden")
            @PathVariable UUID id) {
        log.info("GET /orders/{} - Buscando orden por ID", id);
        
        return orderService.getOrderById(id)
            .map(order -> {
                log.info("Orden encontrada: {}", order.getOrderNumber());
                return ResponseEntity.ok(order);
            })
            .orElseGet(() -> {
                log.warn("Orden no encontrada con ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener orden por número de orden", 
               description = "Busca una orden específica por su número de orden único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderDto> getOrderByOrderNumber(
            @Parameter(description = "Número de orden único", example = "ORD-2024-001")
            @PathVariable String orderNumber) {
        log.info("GET /orders/number/{} - Buscando orden por número", orderNumber);
        
        return orderService.getOrderByOrderNumber(orderNumber)
            .map(order -> {
                log.info("Orden encontrada: {}", order.getOrderNumber());
                return ResponseEntity.ok(order);
            })
            .orElseGet(() -> {
                log.warn("Orden no encontrada con número: {}", orderNumber);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Obtener todas las órdenes", 
               description = "Retorna todas las órdenes con paginación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @Parameter(description = "Parámetros de paginación")
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /orders - Obteniendo todas las órdenes con paginación");
        
        Page<OrderDto> orders = orderService.getAllOrders(pageable);
        
        log.info("Retornando {} órdenes (página {})", orders.getNumberOfElements(), orders.getNumber());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Obtener órdenes de un usuario", 
               description = "Retorna todas las órdenes de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(
            @Parameter(description = "ID del usuario")
            @PathVariable UUID userId) {
        log.info("GET /orders/user/{} - Obteniendo órdenes del usuario", userId);
        
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);
        
        log.info("Retornando {} órdenes para el usuario {}", orders.size(), userId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Obtener órdenes de un usuario con paginación", 
               description = "Retorna las órdenes de un usuario con paginación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<OrderDto>> getOrdersByUserId(
            @Parameter(description = "ID del usuario")
            @PathVariable UUID userId,
            @Parameter(description = "Parámetros de paginación")
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /orders/user/{}/page - Obteniendo órdenes del usuario con paginación", userId);
        
        Page<OrderDto> orders = orderService.getOrdersByUserId(userId, pageable);
        
        log.info("Retornando {} órdenes para el usuario {} (página {})", 
                orders.getNumberOfElements(), userId, orders.getNumber());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Obtener órdenes por estado", 
               description = "Retorna todas las órdenes con un estado específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(
            @Parameter(description = "Estado de la orden", example = "CONFIRMED")
            @PathVariable String status) {
        log.info("GET /orders/status/{} - Obteniendo órdenes por estado", status);
        
        List<OrderDto> orders = orderService.getOrdersByStatus(status);
        
        log.info("Retornando {} órdenes con estado {}", orders.size(), status);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Obtener órdenes por estado de pago", 
               description = "Retorna todas las órdenes con un estado de pago específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<OrderDto>> getOrdersByPaymentStatus(
            @Parameter(description = "Estado de pago", example = "PAID")
            @PathVariable String paymentStatus) {
        log.info("GET /orders/payment-status/{} - Obteniendo órdenes por estado de pago", paymentStatus);
        
        List<OrderDto> orders = orderService.getOrdersByPaymentStatus(paymentStatus);
        
        log.info("Retornando {} órdenes con estado de pago {}", orders.size(), paymentStatus);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Actualizar estado de orden", 
               description = "Actualiza el estado de una orden existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @Parameter(description = "ID único de la orden")
            @PathVariable UUID id,
            @Parameter(description = "Nuevo estado de la orden", example = "CONFIRMED")
            @RequestParam String status) {
        log.info("PATCH /orders/{}/status - Actualizando estado a {}", id, status);
        
        try {
            OrderDto updatedOrder = orderService.updateOrderStatus(id, status);
            
            log.info("Estado de orden actualizado exitosamente: {} (Nuevo estado: {})", 
                    updatedOrder.getOrderNumber(), status);
            
            return ResponseEntity.ok(updatedOrder);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando estado de orden {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar estado de pago de orden", 
               description = "Actualiza el estado de pago de una orden existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de pago actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<OrderDto> updateOrderPaymentStatus(
            @Parameter(description = "ID único de la orden")
            @PathVariable UUID id,
            @Parameter(description = "Nuevo estado de pago", example = "PAID")
            @RequestParam String paymentStatus) {
        log.info("PATCH /orders/{}/payment-status - Actualizando estado de pago a {}", id, paymentStatus);
        
        try {
            OrderDto updatedOrder = orderService.updateOrderPaymentStatus(id, paymentStatus);
            
            log.info("Estado de pago de orden actualizado exitosamente: {} (Nuevo estado: {})", 
                    updatedOrder.getOrderNumber(), paymentStatus);
            
            return ResponseEntity.ok(updatedOrder);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando estado de pago de orden {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

