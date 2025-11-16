package com.surequinos.surequinos_backend.domain.enums;

/**
 * Enum que representa el estado de un usuario en el sistema
 */
public enum UserStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    DELETED("Eliminado");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

