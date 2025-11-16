package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.AddressDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateAddressRequest;
import com.surequinos.surequinos_backend.application.mapper.AddressMapper;
import com.surequinos.surequinos_backend.domain.entity.Address;
import com.surequinos.surequinos_backend.infrastructure.repository.AddressRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de direcciones
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    /**
     * Obtiene todas las direcciones de un usuario por su email
     */
    public List<AddressDto> getAddressesByUserEmail(String email) {
        log.debug("Buscando direcciones para usuario con email: {}", email);
        
        List<Address> addresses = addressRepository.findByUserEmail(email);
        
        log.info("Encontradas {} direcciones para usuario con email: {}", addresses.size(), email);
        
        return addresses.stream()
            .map(addressMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las direcciones de un usuario por su ID
     */
    public List<AddressDto> getAddressesByUserId(UUID userId) {
        log.debug("Buscando direcciones para usuario con ID: {}", userId);
        
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        
        log.info("Encontradas {} direcciones para usuario con ID: {}", addresses.size(), userId);
        
        return addresses.stream()
            .map(addressMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Crea una nueva dirección para un usuario
     */
    @Transactional
    public AddressDto createAddress(UUID userId, CreateAddressRequest request) {
        log.debug("Creando dirección para usuario ID: {}", userId);
        
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuario no encontrado: " + userId);
        }
        
        // Si esta dirección se marca como por defecto, desmarcar las demás
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            Address currentDefault = addressRepository.findByUserIdAndIsDefaultTrue(userId);
            if (currentDefault != null) {
                currentDefault.setIsDefault(false);
                addressRepository.save(currentDefault);
                log.debug("Dirección por defecto anterior desmarcada: {}", currentDefault.getId());
            }
        } else {
            // Si es la primera dirección del usuario, marcarla como por defecto automáticamente
            long addressCount = addressRepository.countByUserId(userId);
            if (addressCount == 0) {
                request.setIsDefault(true);
                log.debug("Primera dirección del usuario, marcada como por defecto automáticamente");
            }
        }
        
        // Crear la dirección
        Address address = addressMapper.toEntity(request);
        address.setUserId(userId);
        
        Address savedAddress = addressRepository.save(address);
        
        log.info("Dirección creada exitosamente: {} (ID: {}) para usuario: {}", 
                savedAddress.getStreet(), savedAddress.getId(), userId);
        
        return addressMapper.toDto(savedAddress);
    }

    /**
     * Crea una dirección para un usuario durante la creación de una orden
     * Este método es usado internamente por OrderService
     */
    @Transactional
    public AddressDto createAddressForOrder(UUID userId, CreateAddressRequest request) {
        log.debug("Creando dirección para orden - usuario ID: {}", userId);
        
        // Si esta dirección se marca como por defecto, desmarcar las demás
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            Address currentDefault = addressRepository.findByUserIdAndIsDefaultTrue(userId);
            if (currentDefault != null) {
                currentDefault.setIsDefault(false);
                addressRepository.save(currentDefault);
            }
        } else {
            // Si es la primera dirección del usuario, marcarla como por defecto automáticamente
            long addressCount = addressRepository.countByUserId(userId);
            if (addressCount == 0) {
                request.setIsDefault(true);
            }
        }
        
        // Crear la dirección
        Address address = addressMapper.toEntity(request);
        address.setUserId(userId);
        
        Address savedAddress = addressRepository.save(address);
        
        log.info("Dirección creada para orden - ID: {} para usuario: {}", savedAddress.getId(), userId);
        
        return addressMapper.toDto(savedAddress);
    }

    /**
     * Valida que una dirección pertenece a un usuario específico
     */
    public boolean validateAddressBelongsToUser(UUID addressId, UUID userId) {
        log.debug("Validando que dirección {} pertenece a usuario {}", addressId, userId);
        
        return addressRepository.findById(addressId)
            .map(address -> address.getUserId().equals(userId))
            .orElse(false);
    }

    /**
     * Obtiene una dirección por ID
     */
    public Optional<AddressDto> getAddressById(UUID addressId) {
        log.debug("Buscando dirección por ID: {}", addressId);
        
        return addressRepository.findById(addressId)
            .map(addressMapper::toDto);
    }
}

