package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.UserDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateUserRequest;
import com.surequinos.surequinos_backend.application.mapper.UserMapper;
import com.surequinos.surequinos_backend.domain.entity.User;
import com.surequinos.surequinos_backend.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de usuarios
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Obtiene todos los usuarios
     */
    public List<UserDto> getAllUsers() {
        log.debug("Obteniendo todos los usuarios");
        
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<UserDto> getUserById(UUID id) {
        log.debug("Buscando usuario por ID: {}", id);
        
        return userRepository.findById(id)
            .map(userMapper::toDto);
    }

    /**
     * Busca un usuario por email
     */
    public Optional<UserDto> getUserByEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        
        return userRepository.findByEmail(email)
            .map(userMapper::toDto);
    }

    /**
     * Busca un usuario por número de documento
     */
    public Optional<UserDto> getUserByDocumentNumber(String documentNumber) {
        log.debug("Buscando usuario por número de documento: {}", documentNumber);
        
        return userRepository.findByDocumentNumber(documentNumber)
            .map(userMapper::toDto);
    }

    /**
     * Crea un nuevo usuario
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        log.debug("Creando nuevo usuario: {}", request.getEmail());
        
        // Validar que el email sea único
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), null)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.getEmail());
        }
        
        // Validar que el número de documento sea único (si se proporciona)
        if (request.getDocumentNumber() != null && !request.getDocumentNumber().isEmpty()) {
            if (userRepository.existsByDocumentNumberAndIdNot(request.getDocumentNumber(), null)) {
                throw new IllegalArgumentException("Ya existe un usuario con el número de documento: " + request.getDocumentNumber());
            }
        }
        
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        
        log.info("Usuario creado exitosamente: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        
        return userMapper.toDto(savedUser);
    }

    /**
     * Actualiza un usuario existente
     */
    @Transactional
    public UserDto updateUser(UUID id, CreateUserRequest request) {
        log.debug("Actualizando usuario ID: {}", id);
        
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        
        // Validar que el email sea único (excluyendo el usuario actual)
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.getEmail());
        }
        
        // Validar que el número de documento sea único (si se proporciona)
        if (request.getDocumentNumber() != null && !request.getDocumentNumber().isEmpty()) {
            if (userRepository.existsByDocumentNumberAndIdNot(request.getDocumentNumber(), id)) {
                throw new IllegalArgumentException("Ya existe un usuario con el número de documento: " + request.getDocumentNumber());
            }
        }
        
        // Actualizar campos
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        existingUser.setPassword(request.getPassword());
        existingUser.setRoleId(request.getRoleId());
        existingUser.setDocumentNumber(request.getDocumentNumber());
        
        User savedUser = userRepository.save(existingUser);
        
        log.info("Usuario actualizado exitosamente: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        
        return userMapper.toDto(savedUser);
    }

    /**
     * Elimina un usuario
     */
    @Transactional
    public void deleteUser(UUID id) {
        log.debug("Eliminando usuario ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        
        userRepository.delete(user);
        
        log.info("Usuario eliminado exitosamente: {} (ID: {})", user.getEmail(), user.getId());
    }
}

