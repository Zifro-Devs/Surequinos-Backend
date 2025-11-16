package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.UserDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateUserRequest;
import com.surequinos.surequinos_backend.application.mapper.UserMapper;
import com.surequinos.surequinos_backend.domain.entity.Role;
import com.surequinos.surequinos_backend.domain.entity.User;
import com.surequinos.surequinos_backend.domain.enums.UserRole;
import com.surequinos.surequinos_backend.domain.enums.UserStatus;

import java.time.LocalDateTime;
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
     * Obtiene todos los usuarios (excluyendo eliminados)
     */
    public List<UserDto> getAllUsers() {
        log.debug("Obteniendo todos los usuarios");
        
        List<User> users = userRepository.findAll().stream()
            .filter(user -> user.getStatus() != UserStatus.DELETED)
            .toList();
        return users.stream()
            .map(this::enrichUserDto)
            .toList();
    }

    /**
     * Obtiene un usuario por ID (excluyendo eliminados)
     */
    public Optional<UserDto> getUserById(UUID id) {
        log.debug("Buscando usuario por ID: {}", id);
        
        return userRepository.findById(id)
            .filter(user -> user.getStatus() != UserStatus.DELETED)
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
     * Crea un nuevo usuario o reactiva uno eliminado
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        log.debug("Creando nuevo usuario: {}", request.getEmail());
        
        // Validar que la contraseña sea obligatoria para crear usuario
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria para crear un nuevo usuario");
        }
        
        // Verificar si existe un usuario eliminado con el mismo email o documento
        User deletedUser = null;
        
        // Buscar por email (incluyendo eliminados)
        Optional<User> userByEmail = userRepository.findByEmailIncludingDeleted(request.getEmail());
        if (userByEmail.isPresent() && userByEmail.get().getStatus() == UserStatus.DELETED) {
            deletedUser = userByEmail.get();
            log.info("Usuario eliminado encontrado con email: {}. Se reactivará.", request.getEmail());
        }
        
        // Si no se encontró por email, buscar por documento (si se proporciona)
        if (deletedUser == null && request.getDocumentNumber() != null && !request.getDocumentNumber().isEmpty()) {
            Optional<User> userByDoc = userRepository.findByDocumentNumberIncludingDeleted(request.getDocumentNumber());
            if (userByDoc.isPresent() && userByDoc.get().getStatus() == UserStatus.DELETED) {
                deletedUser = userByDoc.get();
                log.info("Usuario eliminado encontrado con documento: {}. Se reactivará.", request.getDocumentNumber());
            }
        }
        
        // Si existe un usuario eliminado, reactivarlo y actualizar sus datos
        if (deletedUser != null) {
            deletedUser.setName(request.getName());
            deletedUser.setEmail(request.getEmail());
            deletedUser.setPhoneNumber(request.getPhoneNumber());
            deletedUser.setDocumentNumber(request.getDocumentNumber());
            deletedUser.setStatus(UserStatus.ACTIVE);
            
            // Obtener o crear el rol
            UUID roleId = getOrCreateRoleId(request.getRole());
            deletedUser.setRoleId(roleId);
            
            // Encriptar la contraseña antes de guardar
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            deletedUser.setPassword(encryptedPassword);
            
            User savedUser = userRepository.save(deletedUser);
            
            log.info("Usuario reactivado exitosamente: {} (ID: {}) con rol: {}", 
                    savedUser.getEmail(), savedUser.getId(), request.getRole());
            
            return enrichUserDto(savedUser);
        }
        
        // Validar que el email sea único (solo usuarios activos)
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
        user.setStatus(UserStatus.ACTIVE);
        
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
        
        // Actualizar contraseña solo si se proporciona una nueva (no vacía)
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            existingUser.setPassword(encryptedPassword);
            log.debug("Contraseña actualizada para usuario ID: {}", id);
        } else {
            log.debug("Contraseña no proporcionada, se mantiene la actual para usuario ID: {}", id);
        }
        
        existingUser.setRoleId(roleId);
        existingUser.setDocumentNumber(request.getDocumentNumber());
        
        User savedUser = userRepository.save(existingUser);
        
        log.info("Usuario actualizado exitosamente: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        
        return enrichUserDto(savedUser);
    }

    /**
     * Elimina un usuario (soft delete - marca como DELETED)
     */
    @Transactional
    public void deleteUser(UUID id) {
        log.debug("Eliminando usuario ID: {} (soft delete)", id);
        
        User user = userRepository.findById(id)
            .filter(u -> u.getStatus() != UserStatus.DELETED)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado o ya eliminado: " + id));
        
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        
        log.info("Usuario marcado como eliminado exitosamente: {} (ID: {})", user.getEmail(), user.getId());
    }

    /**
     * Búsqueda unificada de usuarios con todos los filtros posibles
     */
    public List<UserDto> searchUsers(
            String name, 
            String email, 
            String documentNumber, 
            String phoneNumber,
            List<UserRole> roles,
            List<UserStatus> statuses,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        log.debug("Buscando usuarios - name: {}, email: {}, documentNumber: {}, phoneNumber: {}, roles: {}, statuses: {}, startDate: {}, endDate: {}", 
                name, email, documentNumber, phoneNumber, roles, statuses, startDate, endDate);
        
        // Convertir listas a strings separados por comas
        String rolesStr = (roles != null && !roles.isEmpty()) 
            ? roles.stream().map(UserRole::name).collect(java.util.stream.Collectors.joining(","))
            : null;
        
        String statusesStr = (statuses != null && !statuses.isEmpty())
            ? statuses.stream().map(UserStatus::name).collect(java.util.stream.Collectors.joining(","))
            : null;
        
        // Convertir LocalDateTime a String para la query nativa (formato PostgreSQL)
        String startDateStr = startDate != null ? startDate.toString().replace('T', ' ') : null;
        String endDateStr = endDate != null ? endDate.toString().replace('T', ' ') : null;
        
        List<User> users = userRepository.searchUsers(
            name,
            email,
            documentNumber,
            phoneNumber,
            rolesStr,
            statusesStr,
            startDateStr,
            endDateStr
        );
        
        log.info("Encontrados {} usuarios con los criterios de búsqueda", users.size());
        
        return users.stream()
            .map(this::enrichUserDto)
            .toList();
    }

}

