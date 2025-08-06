package com.codethics.consultoria.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing)
 * 
 * Esta configuración permite que el frontend React (que se ejecuta en
 * localhost:3000) pueda comunicarse con el backend Spring Boot (que se ejecuta
 * en
 * localhost:8080).
 * 
 * PROBLEMA RESUELTO:
 * Sin esta configuración, el navegador bloquearía las peticiones del frontend
 * al backend debido a la política de mismo origen (Same-Origin Policy).
 * 
 * CONFIGURACIÓN ACTUAL:
 * - Orígenes permitidos: http://localhost:3000 y https://localhost:3000
 * - Métodos HTTP permitidos: GET, POST, PUT, DELETE, OPTIONS, etc.
 * - Headers permitidos: Todos (*)
 * - Credenciales: Habilitadas para autenticación
 * - Cache: 1 hora (3600 segundos)
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configuración de CORS para WebMvc
     * 
     * Esta configuración se aplica a todos los endpoints que usan
     * @RequestMapping, @GetMapping, @PostMapping, etc.
     * 
     * @param registry Registro de configuración CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplicar a todos los endpoints
                .allowedOrigins("http://localhost:3000", "https://localhost:3000") // Frontend específico
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*") // Permitir todos los headers
                .exposedHeaders("Authorization", "Content-Type", "X-Requested-With") // Headers expuestos al frontend
                .allowCredentials(true) // Permitir cookies y autenticación
                .maxAge(3600); // Cache de preflight por 1 hora
        
        // Configuración específica para WebSocket de notificaciones
        registry.addMapping("/ws/**")
                .allowedOrigins("http://localhost:3000", "https://localhost:3000")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
        
        System.out.println("✅ CORS configurado correctamente:");
        System.out.println("   - Orígenes permitidos: http://localhost:3000, https://localhost:3000");
        System.out.println("   - Endpoints API: /**");
        System.out.println("   - Endpoints WebSocket: /ws/**");
    }
}