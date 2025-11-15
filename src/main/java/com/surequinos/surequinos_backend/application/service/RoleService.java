package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.domain.entity.Role;
import com.surequinos.surequinos_backend.domain.enums.UserRole;
import com.surequinos.surequinos_backend.infrastructure.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de roles
 * Inicializa los roles básicos al arrancar la aplicación
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Inicializa los roles básicos si no existen
     * Se ejecuta automáticamente al arrancar la aplicación
     */
    @PostConstruct
    @Transactional
    public void initializeRoles() {
        log.info("Inicializando roles del sistema...");
        
        for (UserRole userRole : UserRole.values()) {
            roleRepository.findByUserRole(userRole)
                .orElseGet(() -> {
                    log.info("Creando rol: {}", userRole);
                    Role role = Role.builder()
                        .name(userRole)
                        .description(userRole.getDisplayName())
                        .build();
                    Role savedRole = roleRepository.save(role);
                    log.info("Rol {} creado exitosamente con ID: {}", userRole, savedRole.getId());
                    return savedRole;
                });
        }
        
        log.info("Inicialización de roles completada");
    }

    /**
     * Obtiene todos los roles
     */
    public List<Role> getAllRoles() {
        log.debug("Obteniendo todos los roles");
        return roleRepository.findAll();
    }

    /**
     * Obtiene un rol por su enum
     */
    public Role getRoleByUserRole(UserRole userRole) {
        log.debug("Buscando rol: {}", userRole);
        return roleRepository.findByUserRole(userRole)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + userRole));
    }
}

