package com.xperiecia.consultoria.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing)
 * 
 * Esta configuración permite que el frontend React (que se ejecuta en
 * localhost:3000) pueda comunicarse con el backend Spring Boot (que se ejecuta
 * en localhost:8080).
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

        // Obtener orígenes permitidos desde variable de entorno o usar defaults
        private static final String ENV_ALLOWED_ORIGINS = System.getenv("CORS_ALLOWED_ORIGINS");

        // Helper method to merge env vars with defaults
        private static List<String> getAllowedOrigins() {
                List<String> origins = new java.util.ArrayList<>();

                // Add Env vars if present
                if (ENV_ALLOWED_ORIGINS != null && !ENV_ALLOWED_ORIGINS.isEmpty()) {
                        origins.addAll(Arrays.asList(ENV_ALLOWED_ORIGINS.split(",")));
                }

                // Add defaults (Localhost)
                List<String> defaults = Arrays.asList(
                                "http://localhost:3000",
                                "https://localhost:3000",
                                "http://127.0.0.1:3000",
                                "https://127.0.0.1:3000",
                                "http://localhost:5173",
                                "https://localhost:5173",
                                "http://127.0.0.1:5173",
                                "https://127.0.0.1:5173",
                                "http://localhost:5174",
                                "https://localhost:5174",
                                "http://127.0.0.1:5174",
                                "https://127.0.0.1:5174",
                                "https://xperiecia-consulting.vercel.app",
                                "https://xperiecia.com",
                                "https://www.xperiecia.com",
                                "http://xperiecia.com",
                                "https://xperiecia.es",
                                "https://www.xperiecia.es",
                                "http://xperiecia.es");

                origins.addAll(defaults);
                return origins;
        }

        private static final List<String> ALLOWED_ORIGINS = getAllowedOrigins();

        private static final List<String> ALLOWED_METHODS = Arrays.asList(
                        "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD", "TRACE", "CONNECT");

        private static final List<String> ALLOWED_HEADERS = Arrays.asList(
                        "*");

        private static final List<String> EXPOSED_HEADERS = Arrays.asList(
                        "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin");

        /**
         * Configuración de CORS para WebMvc
         * 
         * Esta configuración se aplica a todos los endpoints que usan
         * @RequestMapping, @GetMapping, @PostMapping, etc.
         * 
         * @param registry Registro de configuración CORS
         */
        @SuppressWarnings("null")
        @Override
        public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**") // Aplicar a todos los endpoints
                                .allowedOriginPatterns("*") // Permitir todos los orígenes
                                .allowedMethods(ALLOWED_METHODS.toArray(new String[0]))
                                .allowedHeaders(ALLOWED_HEADERS.toArray(new String[0])) // Permitir todos los headers
                                .exposedHeaders(EXPOSED_HEADERS.toArray(new String[0])) // Headers expuestos al frontend
                                .allowCredentials(true) // Permitir cookies y autenticación
                                .maxAge(3600); // Cache de preflight por 1 hora

                // Configuración específica para WebSocket de notificaciones
                registry.addMapping("/ws/**")
                                .allowedOriginPatterns("*")
                                .allowedMethods("*")
                                .allowedHeaders("*")
                                .allowCredentials(true);

                System.out.println("✅ CORS configurado correctamente (PERMISIVO):");
                System.out.println("   - Orígenes permitidos: * (Pattern)");
                System.out.println("   - Métodos permitidos: " + String.join(", ", ALLOWED_METHODS));
                System.out.println("   - Endpoints API: /**");
                System.out.println("   - Endpoints WebSocket: /ws/**");
        }

        /**
         * Configuración de CORS para Spring Security
         * 
         * Esta configuración se usa específicamente por Spring Security
         * para manejar las peticiones preflight y CORS.
         * 
         * @return CorsConfigurationSource configurado
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Configurar orígenes permitidos (Patrón * para máxima compatibilidad) - Force
                // Redeploy
                configuration.setAllowedOriginPatterns(Arrays.asList("*"));

                // Configurar métodos permitidos
                configuration.setAllowedMethods(ALLOWED_METHODS);

                // Configurar headers permitidos
                configuration.setAllowedHeaders(ALLOWED_HEADERS);

                // Configurar headers expuestos
                configuration.setExposedHeaders(EXPOSED_HEADERS);

                // Permitir credenciales
                configuration.setAllowCredentials(true);

                // Configurar tiempo de cache para preflight
                configuration.setMaxAge(3600L);

                // Configurar el origen de la configuración
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                System.out.println("✅ CorsConfigurationSource configurado para Spring Security");
                return source;
        }
}
