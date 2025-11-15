package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.OrderDto;
import com.surequinos.surequinos_backend.application.dto.OrderItemDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateOrderRequest;
import com.surequinos.surequinos_backend.application.mapper.OrderItemMapper;
import com.surequinos.surequinos_backend.application.mapper.OrderMapper;
import com.surequinos.surequinos_backend.domain.entity.Order;
import com.surequinos.surequinos_backend.domain.entity.OrderItem;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import com.surequinos.surequinos_backend.infrastructure.repository.OrderItemRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.OrderRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.UserRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de órdenes
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final VariantRepository variantRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    /**
     * Genera un número de orden único
     */
    private String generateOrderNumber() {
        String prefix = "ORD";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int)(Math.random() * 1000));
        return String.format("%s-%s-%s", prefix, timestamp, random);
    }

    /**
     * Crea una nueva orden
     */
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        log.debug("Creando nueva orden para usuario: {}", request.getUserId());
        
        // Validar que el usuario exista
        if (!userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("El usuario no existe: " + request.getUserId());
        }
        
        // Validar y procesar items
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = request.getItems().stream()
            .map(itemRequest -> {
                // Validar que la variante exista
                Variant variant = variantRepository.findById(itemRequest.getVariantId())
                    .orElseThrow(() -> new IllegalArgumentException("La variante no existe: " + itemRequest.getVariantId()));
                
                // Validar que la variante esté activa
                if (!variant.getIsActive()) {
                    throw new IllegalArgumentException("La variante no está activa: " + itemRequest.getVariantId());
                }
                
                // Validar stock disponible
                if (variant.getStock() < itemRequest.getQuantity()) {
                    throw new IllegalArgumentException(
                        String.format("Stock insuficiente para la variante %s. Disponible: %d, Solicitado: %d",
                            variant.getSku(), variant.getStock(), itemRequest.getQuantity()));
                }
                
                // Calcular precios
                BigDecimal unitPrice = variant.getPrice();
                BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                
                // Crear OrderItem
                OrderItem orderItem = OrderItem.builder()
                    .variantId(variant.getId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .build();
                
                return orderItem;
            })
            .collect(Collectors.toList());
        
        // Calcular subtotal
        subtotal = orderItems.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcular total
        BigDecimal total = subtotal
            .subtract(request.getDiscountValue() != null ? request.getDiscountValue() : BigDecimal.ZERO)
            .add(request.getShippingValue() != null ? request.getShippingValue() : BigDecimal.ZERO);
        
        // Generar número de orden único
        String orderNumber = generateOrderNumber();
        while (orderRepository.findByOrderNumber(orderNumber).isPresent()) {
            orderNumber = generateOrderNumber();
        }
        
        // Crear la orden
        Order order = Order.builder()
            .orderNumber(orderNumber)
            .userId(request.getUserId())
            .discountValue(request.getDiscountValue() != null ? request.getDiscountValue() : BigDecimal.ZERO)
            .notes(request.getNotes())
            .paymentStatus("PENDING")
            .shippingValue(request.getShippingValue() != null ? request.getShippingValue() : BigDecimal.ZERO)
            .status("PENDING")
            .subtotal(subtotal)
            .total(total)
            .shippingAddress(request.getShippingAddress())
            .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Asignar orderId a los items y guardarlos
        orderItems.forEach(item -> {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);
        });
        
        // Reducir stock de las variantes
        orderItems.forEach(item -> {
            variantRepository.reduceStock(item.getVariantId(), item.getQuantity());
        });
        
        log.info("Orden creada exitosamente: {} (ID: {})", savedOrder.getOrderNumber(), savedOrder.getId());
        
        return enrichOrderDto(savedOrder);
    }

    /**
     * Obtiene una orden por ID
     */
    public Optional<OrderDto> getOrderById(UUID id) {
        log.debug("Buscando orden por ID: {}", id);
        
        return orderRepository.findById(id)
            .map(this::enrichOrderDto);
    }

    /**
     * Obtiene una orden por número de orden
     */
    public Optional<OrderDto> getOrderByOrderNumber(String orderNumber) {
        log.debug("Buscando orden por número: {}", orderNumber);
        
        return orderRepository.findByOrderNumber(orderNumber)
            .map(this::enrichOrderDto);
    }

    /**
     * Obtiene todas las órdenes de un usuario
     */
    public List<OrderDto> getOrdersByUserId(UUID userId) {
        log.debug("Obteniendo órdenes del usuario: {}", userId);
        
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
            .map(this::enrichOrderDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene órdenes de un usuario con paginación
     */
    public Page<OrderDto> getOrdersByUserId(UUID userId, Pageable pageable) {
        log.debug("Obteniendo órdenes del usuario: {} con paginación", userId);
        
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::enrichOrderDto);
    }

    /**
     * Obtiene todas las órdenes con paginación
     */
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        log.debug("Obteniendo todas las órdenes con paginación");
        
        Page<Order> orders = orderRepository.findAllOrderedByCreatedAt(pageable);
        return orders.map(this::enrichOrderDto);
    }

    /**
     * Obtiene órdenes por estado
     */
    public List<OrderDto> getOrdersByStatus(String status) {
        log.debug("Obteniendo órdenes por estado: {}", status);
        
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
            .map(this::enrichOrderDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene órdenes por estado de pago
     */
    public List<OrderDto> getOrdersByPaymentStatus(String paymentStatus) {
        log.debug("Obteniendo órdenes por estado de pago: {}", paymentStatus);
        
        List<Order> orders = orderRepository.findByPaymentStatus(paymentStatus);
        return orders.stream()
            .map(this::enrichOrderDto)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de una orden
     */
    @Transactional
    public OrderDto updateOrderStatus(UUID id, String status) {
        log.debug("Actualizando estado de orden ID: {} a {}", id, status);
        
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + id));
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        log.info("Estado de orden actualizado exitosamente: {} (Nuevo estado: {})", 
                savedOrder.getOrderNumber(), status);
        
        return enrichOrderDto(savedOrder);
    }

    /**
     * Actualiza el estado de pago de una orden
     */
    @Transactional
    public OrderDto updateOrderPaymentStatus(UUID id, String paymentStatus) {
        log.debug("Actualizando estado de pago de orden ID: {} a {}", id, paymentStatus);
        
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + id));
        
        order.setPaymentStatus(paymentStatus);
        Order savedOrder = orderRepository.save(order);
        
        log.info("Estado de pago de orden actualizado exitosamente: {} (Nuevo estado: {})", 
                savedOrder.getOrderNumber(), paymentStatus);
        
        return enrichOrderDto(savedOrder);
    }

    /**
     * Enriquece un OrderDto con información adicional
     */
    private OrderDto enrichOrderDto(Order order) {
        OrderDto dto = orderMapper.toDto(order);
        
        // Cargar items de la orden
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDto> itemDtos = items.stream()
            .map(item -> {
                OrderItemDto itemDto = orderItemMapper.toDto(item);
                
                // Enriquecer con información de la variante
                variantRepository.findById(item.getVariantId())
                    .ifPresent(variant -> {
                        itemDto.setVariantSku(variant.getSku());
                        if (variant.getProduct() != null) {
                            itemDto.setProductName(variant.getProduct().getName());
                        }
                    });
                
                return itemDto;
            })
            .collect(Collectors.toList());
        
        dto.setOrderItems(itemDtos);
        
        // Enriquecer con información del usuario
        if (order.getUser() != null) {
            dto.setUserName(order.getUser().getName());
            dto.setUserEmail(order.getUser().getEmail());
        } else if (order.getUserId() != null) {
            userRepository.findById(order.getUserId())
                .ifPresent(user -> {
                    dto.setUserName(user.getName());
                    dto.setUserEmail(user.getEmail());
                });
        }
        
        return dto;
    }
}

