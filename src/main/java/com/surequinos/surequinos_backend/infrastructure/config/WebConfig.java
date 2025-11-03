package com.surequinos.surequinos_backend.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para CORS y otros aspectos de la API
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",  // Frontend en desarrollo
                "http://localhost:3001",  // Frontend alternativo
                "http://127.0.0.1:3000",  // Localhost alternativo
                "https://surequinos.com", // Dominio de producción
                "https://www.surequinos.com"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false)  // Cambiado a false para evitar problemas con multipart
            .maxAge(3600);
    }
}