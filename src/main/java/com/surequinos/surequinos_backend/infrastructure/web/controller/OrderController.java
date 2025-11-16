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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
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
               description = "Crea una nueva orden de compra. Busca el cliente por email y documento, o lo crea si no existe. Valida stock, calcula totales y reduce inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @Parameter(description = "Datos de la nueva orden")
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /orders - Creando nueva orden para cliente con email: {} y documento: {}", 
                request.getEmail(), request.getDocumentNumber());
        
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
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /orders - Obteniendo todas las órdenes con paginación");
        
        // Validar y normalizar el Pageable para evitar errores con sorts inválidos
        Pageable validPageable = normalizePageable(pageable);
        
        Page<OrderDto> orders = orderService.getAllOrders(validPageable);
        
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
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /orders/user/{}/page - Obteniendo órdenes del usuario con paginación", userId);
        
        // Validar y normalizar el Pageable (misma lógica que getAllOrders)
        Pageable validPageable = normalizePageable(pageable);
        
        Page<OrderDto> orders = orderService.getOrdersByUserId(userId, validPageable);
        
        log.info("Retornando {} órdenes para el usuario {} (página {})", 
                orders.getNumberOfElements(), userId, orders.getNumber());
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Normaliza un Pageable para queries nativas, validando y corrigiendo sorts inválidos
     */
    private Pageable normalizePageable(Pageable pageable) {
        try {
            log.debug("Normalizando Pageable - Sort original: {}", pageable.getSort());
            
            // Si no hay sort o está vacío, usar el por defecto
            if (pageable.getSort().isUnsorted() || pageable.getSort().isEmpty()) {
                log.debug("Sort vacío o no ordenado, usando sort por defecto");
                return PageRequest.of(
                    pageable.getPageNumber(), 
                    pageable.getPageSize(), 
                    JpaSort.unsafe(Sort.Direction.DESC, "created_at")
                );
            }
            
            // Procesar cada orden de sort
            java.util.List<Sort.Order> validOrders = new java.util.ArrayList<>();
            
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                log.debug("Procesando sort order - property: {}, direction: {}", property, order.getDirection());
                
                // Validar que la propiedad no esté vacía o sea inválida
                if (property == null || property.isEmpty() || 
                    property.equals("asc") || property.equals("desc") ||
                    property.equals("ASC") || property.equals("DESC") ||
                    property.contains("[") || property.contains("]")) {
                    log.warn("Saltando sort inválido: {}", property);
                    continue; // Saltar sorts inválidos
                }
                
                // Mapear nombres de propiedades Java a nombres de columnas de BD
                String columnName = switch (property) {
                    case "createdAt" -> "created_at";
                    case "updatedAt" -> "updated_at";
                    case "orderNumber" -> "order_number";
                    case "total" -> "total";
                    case "status" -> "status";
                    case "paymentStatus" -> "payment_status";
                    default -> {
                        log.debug("Usando nombre de columna original para propiedad: {}", property);
                        yield property; // Si no hay mapeo, usar el nombre original
                    }
                };
                
                // Crear un nuevo Sort.Order con el nombre de columna mapeado
                validOrders.add(Sort.Order.by(columnName).with(order.getDirection()));
                log.debug("Sort válido agregado - columna: {}, dirección: {}", columnName, order.getDirection());
            }
            
            // Si no hay sorts válidos, usar el por defecto
            if (validOrders.isEmpty()) {
                log.debug("No hay sorts válidos, usando sort por defecto");
                return PageRequest.of(
                    pageable.getPageNumber(), 
                    pageable.getPageSize(), 
                    JpaSort.unsafe(Sort.Direction.DESC, "created_at")
                );
            }
            
            // Usar solo el primer sort válido para evitar problemas con JpaSort.and()
            // JpaSort.and() devuelve un Sort regular que no funciona bien con queries nativas
            Sort.Order firstOrder = validOrders.get(0);
            JpaSort finalSort = JpaSort.unsafe(firstOrder.getDirection(), firstOrder.getProperty());
            
            // Si hay más de un sort válido, loguear una advertencia
            if (validOrders.size() > 1) {
                log.debug("Múltiples sorts proporcionados, usando solo el primero: {}", firstOrder.getProperty());
            }
            
            return PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                finalSort
            );
            
        } catch (Exception e) {
            // Si hay algún error con el sort, usar el por defecto
            log.warn("Error procesando sort, usando sort por defecto: {}", e.getMessage());
            return PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                JpaSort.unsafe(Sort.Direction.DESC, "created_at")
            );
        }
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

    @Operation(summary = "Buscar órdenes", 
               description = "Busca órdenes por múltiples criterios: ID de orden, número de orden, nombre del cliente, email, documento o número de teléfono. Todos los parámetros son opcionales y se combinan con AND (todos deben cumplirse). Las búsquedas de texto son parciales (LIKE).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes encontradas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/search")
    public ResponseEntity<List<OrderDto>> searchOrders(
            @Parameter(description = "ID único de la orden (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam(required = false) UUID orderId,
            @Parameter(description = "Número de orden (búsqueda parcial)", example = "ORD-2024")
            @RequestParam(required = false) String orderNumber,
            @Parameter(description = "Nombre del cliente (búsqueda parcial, case-insensitive)", example = "Juan")
            @RequestParam(required = false) String clientName,
            @Parameter(description = "Email del cliente (búsqueda parcial, case-insensitive)", example = "cliente@example.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Número de documento del cliente (búsqueda parcial)", example = "1234567890")
            @RequestParam(required = false) String documentNumber,
            @Parameter(description = "Número de teléfono del cliente (búsqueda parcial)", example = "3001234567")
            @RequestParam(required = false) String phoneNumber) {
        log.info("GET /orders/search - Buscando órdenes con criterios: orderId={}, orderNumber={}, clientName={}, email={}, documentNumber={}, phoneNumber={}", 
                orderId, orderNumber, clientName, email, documentNumber, phoneNumber);
        
        List<OrderDto> orders = orderService.searchOrders(orderId, orderNumber, clientName, email, documentNumber, phoneNumber);
        
        log.info("Retornando {} órdenes encontradas", orders.size());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Obtener órdenes por rango de fechas", 
               description = "Obtiene todas las órdenes creadas dentro de un rango de fechas específico. Incluye las fechas de inicio y fin (inclusive).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente"),
        @ApiResponse(responseCode = "400", description = "Fechas inválidas o formato incorrecto"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderDto>> getOrdersByDateRange(
            @Parameter(description = "Fecha de inicio (ISO 8601)", example = "2024-11-01T00:00:00")
            @RequestParam String startDate,
            @Parameter(description = "Fecha de fin (ISO 8601)", example = "2024-11-30T23:59:59")
            @RequestParam String endDate) {
        log.info("GET /orders/date-range - Obteniendo órdenes desde {} hasta {}", startDate, endDate);
        
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate);
            
            // Validar que la fecha de inicio sea anterior a la fecha de fin
            if (start.isAfter(end)) {
                log.warn("Fecha de inicio es posterior a la fecha de fin");
                return ResponseEntity.badRequest().build();
            }
            
            List<OrderDto> orders = orderService.getOrdersByDateRange(start, end);
            
            log.info("Retornando {} órdenes en el rango de fechas", orders.size());
            return ResponseEntity.ok(orders);
            
        } catch (java.time.format.DateTimeParseException e) {
            log.warn("Error parseando fechas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
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

