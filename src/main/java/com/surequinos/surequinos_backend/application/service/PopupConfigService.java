package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.PopupConfigDto;
import com.surequinos.surequinos_backend.domain.entity.PopupConfig;
import com.surequinos.surequinos_backend.infrastructure.repository.PopupConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PopupConfigService {

    private final PopupConfigRepository popupConfigRepository;

    public PopupConfigDto getActiveConfig() {
        log.debug("Obteniendo configuración activa del popup");
        
        PopupConfig config = popupConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultConfig);
        
        return mapToDto(config);
    }

    @Transactional
    public PopupConfigDto updateConfig(PopupConfigDto dto) {
        log.debug("Actualizando configuración del popup: {}", dto);
        
        // Siempre buscar la configuración activa existente o crear una nueva
        PopupConfig config = popupConfigRepository.findActiveConfig()
                .orElseGet(() -> {
                    log.info("No existe configuración activa, creando nueva");
                    return new PopupConfig();
                });
        
        // Actualizar todos los campos
        config.setIsActive(dto.getIsActive());
        config.setTitle(dto.getTitle());
        config.setSubtitle(dto.getSubtitle());
        config.setDescription(dto.getDescription());
        config.setImageUrl(dto.getImageUrl());
        config.setButton1Text(dto.getButton1Text());
        config.setButton1Link(dto.getButton1Link());
        config.setButton2Text(dto.getButton2Text());
        config.setButton2Link(dto.getButton2Link());
        config.setShowButton2(dto.getShowButton2());
        
        PopupConfig saved = popupConfigRepository.save(config);
        
        log.info("Configuración del popup actualizada exitosamente: {}", saved.getId());
        
        return mapToDto(saved);
    }

    private PopupConfig createDefaultConfig() {
        log.info("Creando configuración por defecto del popup");
        
        PopupConfig config = PopupConfig.builder()
                .isActive(true)
                .title("Bienvenido a Surequinos")
                .subtitle("Descubre el arte de la talabartería artesanal colombiana")
                .description("Más de 12 años creando piezas únicas con materiales de primera calidad. Cada silla y tereco es trabajado a mano con dedicación y respeto por la tradición.")
                .imageUrl("/silla-de-montar-artesanal-.jpg")
                .button1Text("Explorar Tienda")
                .button1Link("/tienda")
                .button2Text("Ver Ofertas")
                .button2Link("/sale")
                .showButton2(true)
                .build();
        
        return popupConfigRepository.save(config);
    }

    private PopupConfigDto mapToDto(PopupConfig config) {
        return PopupConfigDto.builder()
                .id(config.getId())
                .isActive(config.getIsActive())
                .title(config.getTitle())
                .subtitle(config.getSubtitle())
                .description(config.getDescription())
                .imageUrl(config.getImageUrl())
                .button1Text(config.getButton1Text())
                .button1Link(config.getButton1Link())
                .button2Text(config.getButton2Text())
                .button2Link(config.getButton2Link())
                .showButton2(config.getShowButton2())
                .build();
    }
}
