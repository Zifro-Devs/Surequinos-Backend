package com.surequinos.surequinos_backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidad que representa las opciones de atributos disponibles
 * (colores, tallas, tipos, etc.)
 */
@Entity
@Table(name = "attribute_options", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"attribute_name", "value"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "attribute_name", nullable = false, length = 50)
    private String attributeName;

    @Column(name = "value", nullable = false, length = 100)
    private String value;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}