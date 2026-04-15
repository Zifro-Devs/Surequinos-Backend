package com.surequinos.surequinos_backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstagramPostDto {
    private UUID id;
    private String postUrl;
    private Boolean isActive;
    private Integer displayOrder;
}
