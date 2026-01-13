package com.xperiecia.consultoria.api;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador que simula funcionalidad de WebSocket usando endpoints REST
 * 
 * PROBLEMA ORIGINAL:
 * El frontend intentaba conectarse a WebSocket en ws://localhost:3000/ws
 * pero no había servidor WebSocket implementado, causando errores de conexión.
 * 
 * SOLUCIÓN IMPLEMENTADA:
 * Se crearon endpoints REST que simulan la funcionalidad de WebSocket,
 * permitiendo que el frontend use fetch() en lugar de WebSocket.
 * 
 * ADAPTACIÓN FRONTEND:
 * El frontend debe cambiar de:
 * const ws = new WebSocket('ws://localhost:3000/ws');
 * A:
 * const response = await fetch('http://localhost:8080/api/ws/status');
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/ws")
@Tag(name = "WebSocket Simulator", description = "Simula funcionalidad de WebSocket para el frontend")
public class WebSocketSimulatorController {

    /**
     * Endpoint que simula el estado de conexión de WebSocket
     * 
     * Este endpoint reemplaza la funcionalidad de verificación de conexión
     * que normalmente se haría con WebSocket. El frontend puede usar este
     * endpoint para verificar si el "servidor WebSocket" está activo.
     * 
     * @return Mapa con información del estado de conexión
     */
    @GetMapping("/status")
    @Operation(summary = "Estado de conexión WebSocket")
    public Map<String, Object> getWebSocketStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "connected");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", "WebSocket simulator active");
        return response;
    }

    /**
     * Endpoint que simula el envío de mensajes a través de WebSocket
     * 
     * Este endpoint reemplaza la funcionalidad de envío de mensajes
     * que normalmente se haría con WebSocket. El frontend puede usar
     * este endpoint para enviar mensajes al "servidor WebSocket".
     * 
     * @param message Mensaje a procesar (puede ser cualquier estructura JSON)
     * @return Mapa con confirmación de recepción y timestamp
     */
    @PostMapping("/message")
    @Operation(summary = "Enviar mensaje a través del simulador")
    public Map<String, Object> sendMessage(@RequestBody Map<String, Object> message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "received");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("received", message);
        response.put("message", "Mensaje procesado correctamente");
        return response;
    }
}
