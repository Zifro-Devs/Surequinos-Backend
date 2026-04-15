package com.surequinos.surequinos_backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "popup_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "subtitle", length = 300)
    private String subtitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // Botón 1
    @Column(name = "button1_text", length = 100)
    private String button1Text;

    @Column(name = "button1_link", length = 500)
    private String button1Link;

    // Botón 2 (opcional)
    @Column(name = "button2_text", length = 100)
    private String button2Text;

    @Column(name = "button2_link", length = 500)
    private String button2Link;

    @Column(name = "show_button2")
    @Builder.Default
    private Boolean showButton2 = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
