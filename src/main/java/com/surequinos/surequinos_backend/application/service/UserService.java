package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.UserDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateUserRequest;
import com.surequinos.surequinos_backend.application.mapper.UserMapper;
import com.surequinos.surequinos_backend.domain.entity.Role;
import com.surequinos.surequinos_backend.domain.entity.User;
import com.surequinos.surequinos_backend.domain.enums.UserRole;
import com.surequinos.surequinos_backend.infrastructure.repository.RoleRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Enriquece un UserDto con información del rol
     */
    private UserDto enrichUserDto(User user) {
        UserDto dto = userMapper.toDto(user);
        
        // Enriquecer con información del rol
        if (user.getRole() != null) {
            dto.setRole(user.getRole().getName());
        } else if (user.getRoleId() != null) {
            roleRepository.findById(user.getRoleId())
                .ifPresent(role -> dto.setRole(role.getName()));
        }
        
        return dto;
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<UserDto> getAllUsers() {
        log.debug("Obteniendo todos los usuarios");
        
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(this::enrichUserDto)
            .toList();
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<UserDto> getUserById(UUID id) {
        log.debug("Buscando usuario por ID: {}", id);
        
        return userRepository.findById(id)
            .map(this::enrichUserDto);
    }

    /**
     * Busca un usuario por email
     */
    public Optional<UserDto> getUserByEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        
        return userRepository.findByEmail(email)
            .map(this::enrichUserDto);
    }

    /**
     * Busca un usuario por número de documento
     */
    public Optional<UserDto> getUserByDocumentNumber(String documentNumber) {
        log.debug("Buscando usuario por número de documento: {}", documentNumber);
        
        return userRepository.findByDocumentNumber(documentNumber)
            .map(this::enrichUserDto);
    }

    /**
     * Obtiene o crea un rol basado en el enum UserRole
     */
    private UUID getOrCreateRoleId(UserRole userRole) {
        return roleRepository.findByUserRole(userRole)
            .map(Role::getId)
            .orElseGet(() -> {
                log.warn("Rol {} no encontrado en la base de datos. Creándolo...", userRole);
                Role newRole = Role.builder()
                    .name(userRole)
                    .description(userRole.getDisplayName())
                    .build();
                Role savedRole = roleRepository.save(newRole);
                log.info("Rol {} creado con ID: {}", userRole, savedRole.getId());
                return savedRole.getId();
            });
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
        
        // Obtener o crear el rol
        UUID roleId = getOrCreateRoleId(request.getRole());
        
        // Crear el usuario
        User user = userMapper.toEntity(request);
        user.setRoleId(roleId);
        
        // Encriptar la contraseña antes de guardar
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encryptedPassword);
        
        User savedUser = userRepository.save(user);
        
        log.info("Usuario creado exitosamente: {} (ID: {}) con rol: {}", 
                savedUser.getEmail(), savedUser.getId(), request.getRole());
        
        return enrichUserDto(savedUser);
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
        
        // Obtener o crear el rol
        UUID roleId = getOrCreateRoleId(request.getRole());
        
        // Actualizar campos
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        
        // Encriptar la contraseña antes de actualizar (siempre se encripta, incluso si es la misma)
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        existingUser.setPassword(encryptedPassword);
        
        existingUser.setRoleId(roleId);
        existingUser.setDocumentNumber(request.getDocumentNumber());
        
        User savedUser = userRepository.save(existingUser);
        
        log.info("Usuario actualizado exitosamente: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        
        return enrichUserDto(savedUser);
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

