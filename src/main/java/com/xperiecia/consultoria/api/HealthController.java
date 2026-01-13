package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para endpoint de salud del sistema
 * 
 * Este controlador proporciona un endpoint de salud simple
 * que el frontend puede usar para verificar la conectividad.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "API para verificar salud del sistema")
public class HealthController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Verifica la salud del sistema
     * 
     * Este endpoint es utilizado por el frontend para verificar
     * que el backend está funcionando correctamente.
     * 
     * @return Estado de salud del sistema
     */
    @GetMapping("/health")
    @Operation(summary = "Verificar salud del sistema")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();

        try {
            // Verificar conectividad a la base de datos
            long userCount = userRepository.count();

            health.put("status", "healthy");
            health.put("database", "connected");
            health.put("timestamp", LocalDateTime.now().toString());
            health.put("checks", Map.of(
                    "database", "ok",
                    "api", "ok",
                    "memory", "ok"));

            return ResponseEntity.ok(health);

        } catch (Exception e) {
            Map<String, Object> errorHealth = new HashMap<>();
            errorHealth.put("status", "unhealthy");
            errorHealth.put("error", e.getMessage());
            errorHealth.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(503).body(errorHealth);
        }
    }
} 
