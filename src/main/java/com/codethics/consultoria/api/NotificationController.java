package com.codethics.consultoria.api;

import com.codethics.consultoria.application.NotificationService;
import com.codethics.consultoria.domain.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Gestión de notificaciones del sistema")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Obtener notificaciones de un usuario
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener notificaciones de un usuario")
    public ResponseEntity<Page<Notification>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "user") String userRole,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            System.out.println("📥 Obteniendo notificaciones para usuario: " + userId + " con rol: " + userRole);
            Page<Notification> notifications = notificationService.getUserNotifications(userId, userRole, page, size);
            System.out.println("📥 Encontradas " + notifications.getTotalElements() + " notificaciones");
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo notificaciones: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Marcar una notificación como leída
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        try {
            System.out.println("✅ Marcando notificación como leída: " + id);
            notificationService.markAsRead(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación marcada como leída");
            response.put("notificationId", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error marcando notificación como leída: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al marcar la notificación como leída");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Marcar todas las notificaciones como leídas
     */
    @PutMapping("/user/{userId}/read-all")
    @Operation(summary = "Marcar todas las notificaciones como leídas")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "user") String userRole) {

        try {
            System.out.println("✅ Marcando todas las notificaciones como leídas para usuario: " + userId);
            notificationService.markAllAsRead(userId, userRole);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todas las notificaciones han sido marcadas como leídas");
            response.put("userId", userId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error marcando todas las notificaciones como leídas: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al marcar las notificaciones como leídas");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Eliminar una notificación
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una notificación")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        try {
            System.out.println("🗑️ Eliminando notificación: " + id);
            notificationService.deleteNotification(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación eliminada correctamente");
            response.put("notificationId", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error eliminando notificación: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar la notificación");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener estadísticas de notificaciones
     */
    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "Obtener estadísticas de notificaciones")
    public ResponseEntity<Map<String, Object>> getNotificationStats(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "user") String userRole) {

        try {
            System.out.println("📊 Obteniendo estadísticas de notificaciones para usuario: " + userId);
            Map<String, Object> stats = notificationService.getNotificationStats(userId, userRole);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo estadísticas: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crear una nueva notificación (para testing)
     */
    @PostMapping
    @Operation(summary = "Crear una nueva notificación")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        try {
            System.out.println("📢 Creando nueva notificación: " + notification.getTitle());
            System.out.println("📢 Datos recibidos: " + notification.toString());
            Notification created = notificationService.createNotification(notification);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            System.err.println("❌ Error creando notificación: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crear notificación con manejo flexible de datos
     */
    @PostMapping("/create")
    @Operation(summary = "Crear notificación con datos flexibles")
    public ResponseEntity<Map<String, Object>> createNotificationFlexible(@RequestBody Map<String, Object> data) {
        try {
            System.out.println("📢 Creando notificación flexible con datos: " + data);

            // Extraer datos del Map con manejo seguro
            String type = extractString(data, "type", "GENERAL");
            String title = extractString(data, "title", "");
            String message = extractString(data, "message", "");
            String priority = extractString(data, "priority", "medium");

            // Validar campos requeridos
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("El título es requerido");
            }
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("El mensaje es requerido");
            }

            // Crear notificación
            Notification notification = new Notification(type, title, message, priority);

            // Establecer campos opcionales con manejo seguro
            if (data.get("targetUserId") != null) {
                notification.setTargetUserId(extractLong(data, "targetUserId"));
            }
            if (data.get("targetRole") != null) {
                notification.setTargetRole(extractString(data, "targetRole", null));
            }
            if (data.get("relatedEntityId") != null) {
                notification.setRelatedEntityId(extractLong(data, "relatedEntityId"));
            }
            if (data.get("relatedEntityType") != null) {
                notification.setRelatedEntityType(extractString(data, "relatedEntityType", null));
            }

            // Guardar notificación
            Notification created = notificationService.createNotification(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación creada correctamente");
            response.put("notification", created);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creando notificación flexible: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creando notificación");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("receivedData", data);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Crear notificación del sistema (para administradores)
     */
    @PostMapping("/system")
    @Operation(summary = "Crear notificación del sistema")
    public ResponseEntity<Void> createSystemNotification(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(defaultValue = "user") String targetRole) {

        try {
            System.out.println("🔔 Creando notificación del sistema: " + title);
            notificationService.createSystemNotification(title, message, targetRole);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("❌ Error creando notificación del sistema: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crear notificación de bienvenida (para nuevos usuarios)
     */
    @PostMapping("/welcome/{userId}")
    @Operation(summary = "Crear notificación de bienvenida")
    public ResponseEntity<Void> createWelcomeNotification(
            @PathVariable Long userId,
            @RequestParam String userName) {

        try {
            System.out.println("👋 Creando notificación de bienvenida para: " + userName);
            notificationService.createWelcomeNotification(userId, userName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("❌ Error creando notificación de bienvenida: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crear notificación de nuevo presupuesto (específico para presupuestos)
     */
    @PostMapping("/budget")
    @Operation(summary = "Crear notificación de nuevo presupuesto")
    public ResponseEntity<Map<String, Object>> createBudgetNotification(@RequestBody Map<String, Object> data) {
        try {
            System.out.println("📢 Creando notificación de presupuesto con datos: " + data);

            // Extraer datos del Map con manejo seguro
            String title = extractString(data, "title", "Nuevo Presupuesto");
            String message = extractString(data, "message", "Se ha creado un nuevo presupuesto");
            String priority = extractString(data, "priority", "high");
            String targetRole = extractString(data, "targetRole", "admin");
            Long budgetId = extractLong(data, "budgetId");
            String budgetTitle = extractString(data, "budgetTitle", "Presupuesto");

            // Crear notificación
            Notification notification = new Notification("BUDGET_PENDING", title, message, priority);
            notification.setTargetRole(targetRole);

            if (budgetId != null) {
                notification.setRelatedEntityId(budgetId);
                notification.setRelatedEntityType("BUDGET");
            }

            // Guardar notificación
            Notification created = notificationService.createNotification(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación de presupuesto creada correctamente");
            response.put("notification", created);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creando notificación de presupuesto: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creando notificación de presupuesto");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("receivedData", data);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint de test para verificar que el controlador funciona
     */
    @GetMapping("/test")
    @Operation(summary = "Test del controlador de notificaciones")
    public ResponseEntity<Map<String, Object>> testController() {
        try {
            Map<String, Object> response = Map.of(
                    "status", "OK",
                    "message", "Controlador de notificaciones funcionando correctamente",
                    "timestamp", System.currentTimeMillis());
            System.out.println("✅ Test del controlador de notificaciones exitoso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en test del controlador: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de debug para ver qué datos envía el frontend
     */
    @PostMapping("/debug")
    @Operation(summary = "Debug endpoint para ver datos del frontend")
    public ResponseEntity<Map<String, Object>> debugNotification(@RequestBody Map<String, Object> data) {
        try {
            System.out.println("🔍 DEBUG - Datos recibidos del frontend:");
            System.out.println("🔍 Tipo de datos: " + data.getClass().getName());
            System.out.println("🔍 Contenido: " + data.toString());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Datos recibidos correctamente");
            response.put("receivedData", data);
            response.put("dataType", data.getClass().getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en debug: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error procesando datos");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Extraer string de forma segura
     */
    private String extractString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Map) {
            // Si es un objeto, intentar extraer un valor específico
            Map<?, ?> map = (Map<?, ?>) value;
            if (map.containsKey("value")) {
                return map.get("value").toString();
            }
            // Si no hay "value", convertir todo el objeto a string
            return map.toString();
        }
        return value.toString();
    }

    /**
     * Extraer Long de forma segura
     */
    private Long extractLong(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}