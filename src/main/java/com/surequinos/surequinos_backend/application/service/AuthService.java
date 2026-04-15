package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.AuthResponse;
import com.surequinos.surequinos_backend.application.dto.request.LoginRequest;
import com.surequinos.surequinos_backend.application.dto.request.RegisterRequest;
import com.surequinos.surequinos_backend.domain.entity.Role;
import com.surequinos.surequinos_backend.domain.entity.User;
import com.surequinos.surequinos_backend.domain.enums.UserRole;
import com.surequinos.surequinos_backend.domain.enums.UserStatus;
import com.surequinos.surequinos_backend.infrastructure.repository.RoleRepository;
import com.surequinos.surequinos_backend.infrastructure.repository.UserRepository;
import com.surequinos.surequinos_backend.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.debug("Intentando login para email: {}", request.getEmail());

        // Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        // Verificar que el usuario esté activo
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Usuario inactivo o eliminado");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Obtener rol
        String roleName = roleRepository.findById(user.getRoleId())
                .map(role -> role.getName().name())
                .orElse("CLIENTE");

        // Generar token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), roleName);

        log.info("Login exitoso para usuario: {} con rol: {}", user.getEmail(), roleName);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(roleName)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Intentando registrar usuario con email: {}", request.getEmail());

        // Verificar que el email no exista
        Optional<User> existingUserByEmail = userRepository.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Verificar que el documento no exista usando el método más seguro
        try {
            Optional<User> existingUserByDocument = userRepository.findByDocumentNumber(request.getDocumentNumber());
            if (existingUserByDocument.isPresent()) {
                throw new IllegalArgumentException("El número de documento ya está registrado");
            }
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            // Si hay duplicados en la BD, también considerarlo como "ya existe"
            log.warn("Múltiples usuarios encontrados con documento: {}. Rechazando registro.", request.getDocumentNumber());
            throw new IllegalArgumentException("El número de documento ya está registrado");
        }

        // Obtener o crear el rol CLIENTE
        Role clienteRole = roleRepository.findByUserRole(UserRole.CLIENTE)
                .orElseGet(() -> {
                    log.warn("Rol CLIENTE no encontrado. Creándolo...");
                    Role newRole = Role.builder()
                            .name(UserRole.CLIENTE)
                            .description(UserRole.CLIENTE.getDisplayName())
                            .build();
                    return roleRepository.save(newRole);
                });

        // Crear usuario
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .documentNumber(request.getDocumentNumber())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .roleId(clienteRole.getId())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        // Generar token
        String token = jwtTokenProvider.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                UserRole.CLIENTE.name()
        );

        log.info("Usuario registrado exitosamente: {}", savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(UserRole.CLIENTE.name())
                .build();
    }
}
