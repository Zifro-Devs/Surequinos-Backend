package com.surequinos.surequinos_backend.domain.enums;

/**
 * Enum que representa los roles de usuario en el sistema
 */
public enum UserRole {
    ADMIN("Administrador"),
    CLIENTE("Cliente");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

