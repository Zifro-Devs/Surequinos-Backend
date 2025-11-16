package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.OrderDto;
import com.surequinos.surequinos_backend.application.dto.OrderItemDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateAddressRequest;
import com.surequinos.surequinos_backend.application.dto.request.CreateOrderRequest;
import com.surequinos.surequinos_backend.application.mapper.OrderItemMapper;
import com.surequinos.surequinos_backend.application.mapper.OrderMapper;
import com.surequinos.surequinos_backend.application.service.AddressService;
import com.surequinos.surequinos_backend.domain.entity.Order;
import com.surequinos.surequinos_backend.domain.entity.OrderItem;
import com.surequinos.surequinos_backend.domain.entity.Role;
import com.surequinos.surequinos_backend.domain.entity.User;
import com.surequinos.surequinos_backend.domain.entity.Variant;
import com.surequinos.surequinos_backend.domain.enums.UserRole;
import com.surequinos.surequinos_backend.infrastructure.repository.OrderItemRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.OrderRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.RoleRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.UserRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final RoleRepository roleRepository;
    private final VariantRepository variantRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

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
     * Busca o crea un usuario basado en email y documento
     */
    private UUID findOrCreateUser(String email, String documentNumber, String name, String phoneNumber) {
        log.debug("Buscando usuario con email: {} y documento: {}", email, documentNumber);
        
        // Buscar usuario existente por email y documento
        Optional<User> existingUser = userRepository.findByEmailAndDocumentNumber(email, documentNumber);
        
        if (existingUser.isPresent()) {
            log.info("Usuario encontrado: {} (ID: {})", email, existingUser.get().getId());
            return existingUser.get().getId();
        }
        
        // Si no existe, crear nuevo usuario
        log.info("Usuario no encontrado. Creando nuevo usuario con email: {} y documento: {}", email, documentNumber);
        
        // Obtener o crear el rol CLIENTE
        UUID roleId = roleRepository.findByUserRole(UserRole.CLIENTE)
            .map(Role::getId)
            .orElseGet(() -> {
                log.warn("Rol CLIENTE no encontrado. Creándolo...");
                Role newRole = Role.builder()
                    .name(UserRole.CLIENTE)
                    .description(UserRole.CLIENTE.getDisplayName())
                    .build();
                Role savedRole = roleRepository.save(newRole);
                log.info("Rol CLIENTE creado con ID: {}", savedRole.getId());
                return savedRole.getId();
            });
        
        // Crear nuevo usuario con valores por defecto si no se proporcionan
        String userName = (name != null && !name.isEmpty()) ? name : "Cliente " + documentNumber;
        String userPhone = (phoneNumber != null && !phoneNumber.isEmpty()) ? phoneNumber : null;
        
        // La contraseña será el número de documento encriptado
        String password = passwordEncoder.encode(documentNumber);
        
        User newUser = User.builder()
            .name(userName)
            .email(email)
            .documentNumber(documentNumber)
            .phoneNumber(userPhone)
            .password(password)
            .roleId(roleId)
            .build();
        
        User savedUser = userRepository.save(newUser);
        log.info("Usuario creado exitosamente: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        
        return savedUser.getId();
    }

    /**
     * Crea una nueva orden
     */
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        log.debug("Creando nueva orden para cliente con email: {} y documento: {}", 
                request.getEmail(), request.getDocumentNumber());
        
        // Buscar o crear el usuario
        UUID userId = findOrCreateUser(
            request.getEmail(), 
            request.getDocumentNumber(),
            request.getClientName(),
            request.getClientPhoneNumber()
        );
        
        // Manejar dirección: prioridad a addressId sobre address
        if (request.getAddressId() != null) {
            // Validar que la dirección existe y pertenece al usuario
            if (!addressService.validateAddressBelongsToUser(request.getAddressId(), userId)) {
                throw new IllegalArgumentException("La dirección no existe o no pertenece al usuario");
            }
            log.info("Usando dirección existente (ID: {}) para usuario: {}", request.getAddressId(), userId);
        } else if (request.getAddress() != null) {
            // Crear nueva dirección solo si no se proporcionó addressId
            try {
                addressService.createAddressForOrder(userId, request.getAddress());
                log.info("Dirección creada para usuario durante la creación de la orden: {}", userId);
            } catch (Exception e) {
                log.warn("Error al crear dirección durante la creación de la orden: {}. La orden se creará sin guardar la dirección.", e.getMessage());
            }
        } else {
            // No se proporcionó addressId ni address, solo se usa shippingAddress como texto
            log.debug("No se proporcionó dirección para guardar, usando solo shippingAddress como texto");
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
                
                // Nota: Se permite crear órdenes incluso con stock insuficiente (stock negativo permitido)
                if (variant.getStock() < itemRequest.getQuantity()) {
                    log.warn("Stock insuficiente para la variante {} (SKU: {}). Disponible: {}, Solicitado: {}. Se permitirá stock negativo.",
                            itemRequest.getVariantId(), variant.getSku(), variant.getStock(), itemRequest.getQuantity());
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
            .userId(userId)
            .discountValue(request.getDiscountValue() != null ? request.getDiscountValue() : BigDecimal.ZERO)
            .notes(request.getNotes())
            .paymentStatus("PENDING")
            .paymentMethod(request.getPaymentMethod())
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
     * Busca órdenes por múltiples criterios
     */
    public List<OrderDto> searchOrders(UUID orderId, String orderNumber, String clientName, 
                                      String email, String documentNumber, String phoneNumber) {
        log.debug("Buscando órdenes - orderId: {}, orderNumber: {}, clientName: {}, email: {}, documentNumber: {}, phoneNumber: {}", 
                orderId, orderNumber, clientName, email, documentNumber, phoneNumber);
        
        String orderIdStr = orderId != null ? orderId.toString() : null;
        
        List<Order> orders = orderRepository.searchOrders(
            orderIdStr,
            orderNumber,
            clientName,
            email,
            documentNumber,
            phoneNumber
        );
        
        log.info("Encontradas {} órdenes con los criterios de búsqueda", orders.size());
        
        return orders.stream()
            .map(this::enrichOrderDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene órdenes por rango de fechas
     */
    public List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Obteniendo órdenes por rango de fechas: desde {} hasta {}", startDate, endDate);
        
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);
        
        log.info("Encontradas {} órdenes en el rango de fechas", orders.size());
        
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

