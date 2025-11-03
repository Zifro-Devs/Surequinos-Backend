package com.surequinos.surequinos_backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Clase principal de la aplicación Surequinos Backend
 * Ecommerce de productos de talabartería con gestión completa de productos, variantes y categorías
 */
@SpringBootApplication
@Slf4j
@OpenAPIDefinition(
    info = @Info(
        title = "Surequinos Ecommerce API",
        version = "1.0.0",
        description = "API REST para ecommerce de productos de talabartería"
    )
)
public class SurequinosBackendApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SurequinosBackendApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";
        
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("No se pudo determinar la dirección IP del host");
        }

        log.info("""
            
            ----------------------------------------------------------
            	Aplicación '{}' ejecutándose! URLs de acceso:
            	Local: 		{}://localhost:{}{}
            	Externa: 	{}://{}:{}{}
            	Swagger: 	{}://localhost:{}{}/swagger-ui.html
            	Perfil(es): 	{}
            ----------------------------------------------------------""",
            env.getProperty("spring.application.name"),
            protocol, serverPort, contextPath,
            protocol, hostAddress, serverPort, contextPath,
            protocol, serverPort, contextPath,
            env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );
    }
}
