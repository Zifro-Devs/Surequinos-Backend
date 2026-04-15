package com.surequinos.surequinos_backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupConfigDto {
    private UUID id;
    
    @NotNull(message = "isActive no puede ser nulo")
    private Boolean isActive;
    
    @NotBlank(message = "El título es obligatorio")
    private String title;
    
    private String subtitle;
    private String description;
    private String imageUrl;
    private String button1Text;
    private String button1Link;
    private String button2Text;
    private String button2Link;
    
    @NotNull(message = "showButton2 no puede ser nulo")
    private Boolean showButton2;
}
