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
public class NewsItemDto {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private String dateText;
    private Boolean isFeatured;
    private Boolean isActive;
    private Integer displayOrder;
}
