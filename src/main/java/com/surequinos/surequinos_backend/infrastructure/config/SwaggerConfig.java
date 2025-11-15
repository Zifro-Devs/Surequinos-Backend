package com.surequinos.surequinos_backend.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para documentación de la API
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Surequinos Ecommerce API")
                .description("""
                    API REST completa para el ecommerce de productos de talabartería Surequinos.
                    
                    ## Funcionalidades principales:
                    
                    ### Gestión de Productos
                    - Productos con variantes (color, talla, tipo)
                    - Control de stock en tiempo real
                    - Gestión de imágenes con Cloudflare R2
                    - Categorías jerárquicas
                    
                    ### Gestión de Órdenes
                    - Creación de órdenes con múltiples productos
                    - Cálculo automático de subtotales y totales
                    - Gestión de descuentos y costos de envío
                    - Estados de orden y pago configurables
                    - Reducción automática de stock
                    
                    ### Gestión de Usuarios
                    - Sistema de usuarios con roles (ADMIN, CLIENTE)
                    - Autenticación y autorización
                    - Gestión de clientes y administradores
                    
                    ### Otros
                    - Documentación completa con Swagger
                    - Manejo centralizado de excepciones
                    - Validaciones robustas
                    """)
                .version("2.0.0")
                .contact(new Contact()
                    .name("Equipo Surequinos")
                    .email("contacto@surequinos.com")
                    .url("https://surequinos.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080" + contextPath)
                    .description("Servidor de desarrollo local"),
                new Server()
                    .url("https://api.surequinos.com" + contextPath)
                    .description("Servidor de producción")
            ));
    }
}