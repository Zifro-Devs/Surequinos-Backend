package com.surequinos.surequinos_backend.domain.entity;

import com.surequinos.surequinos_backend.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa un rol de usuario
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private UserRole name;

    @Column(name = "description", length = 200)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relación con usuarios
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
}

