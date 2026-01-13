package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.application.AdminNotificationService;
import com.xperiecia.consultoria.domain.Notification;
import com.xperiecia.consultoria.domain.NotificationRepository;
import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "API para administradores del sistema")
public class AdminController {

    @Autowired
    private AdminNotificationService adminNotificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // ========================================
    // ENDPOINTS DE NOTIFICACIONES MANUALES
    // ========================================

    /**
     * Obtener todas las notificaciones de administradores
     */
    @GetMapping("/notifications")
    @Operation(summary = "Obtener notificaciones de administradores")
    public ResponseEntity<Page<Notification>> getAdminNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Notification> notifications = notificationRepository.findByTargetRole("admin", pageable);

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo notificaciones de admin: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Eliminar todas las notificaciones de administradores
     */
    @DeleteMapping("/notifications")
    @Operation(summary = "Eliminar todas las notificaciones de administradores")
    public ResponseEntity<Map<String, Object>> deleteAllNotifications() {
        try {
            // Obtener todas las notificaciones de administradores
            List<Notification> adminNotifications = notificationRepository.findByTargetRole("admin");

            if (adminNotifications.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "No hay notificaciones de administradores para eliminar");
                response.put("deletedCount", 0);
                return ResponseEntity.ok(response);
            }

            // Eliminar todas las notificaciones de administradores
            notificationRepository.deleteByTargetRole("admin");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todas las notificaciones de administradores eliminadas correctamente");
            response.put("deletedCount", adminNotifications.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error eliminando todas las notificaciones: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error eliminando notificaciones: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Eliminar una notificación específica
     */
    @DeleteMapping("/notifications/{id}")
    @Operation(summary = "Eliminar notificación específica")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(id);
            if (notificationOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Notificación no encontrada");
                return ResponseEntity.notFound().build();
            }

            Notification notification = notificationOpt.get();

            // Verificar que la notificación es para administradores
            if (!"admin".equals(notification.getTargetRole())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No autorizado para eliminar esta notificación");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            notificationRepository.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación eliminada correctamente");
            response.put("notificationId", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error eliminando notificación: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error eliminando notificación: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Marcar notificación como leída
     */
    @PutMapping("/notifications/{id}/read")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<Map<String, Object>> markNotificationAsRead(@PathVariable Long id) {
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(id);
            if (notificationOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Notificación no encontrada");
                return ResponseEntity.notFound().build();
            }

            Notification notification = notificationOpt.get();

            // Verificar que la notificación es para administradores
            if (!"admin".equals(notification.getTargetRole())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No autorizado para modificar esta notificación");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            notification.setRead(true);
            notificationRepository.save(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación marcada como leída");
            response.put("notificationId", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error marcando notificación como leída: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error marcando notificación como leída: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Enviar estadísticas diarias manualmente
     */
    @PostMapping("/notifications/daily-stats")
    @Operation(summary = "Enviar estadísticas diarias manualmente")
    public ResponseEntity<Map<String, Object>> sendDailyStatsManually() {
        try {
            adminNotificationService.sendDailyStats();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estadísticas diarias enviadas correctamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando estadísticas diarias: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando estadísticas: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Enviar estadísticas semanales manualmente
     */
    @PostMapping("/notifications/weekly-stats")
    @Operation(summary = "Enviar estadísticas semanales manualmente")
    public ResponseEntity<Map<String, Object>> sendWeeklyStatsManually() {
        try {
            adminNotificationService.sendWeeklyStats();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estadísticas semanales enviadas correctamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando estadísticas semanales: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando estadísticas: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Enviar estadísticas mensuales manualmente
     */
    @PostMapping("/notifications/monthly-stats")
    @Operation(summary = "Enviar estadísticas mensuales manualmente")
    public ResponseEntity<Map<String, Object>> sendMonthlyStatsManually() {
        try {
            adminNotificationService.sendMonthlyStats();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estadísticas mensuales enviadas correctamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando estadísticas mensuales: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando estadísticas: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Simular nuevo registro de usuario (para pruebas)
     */
    @PostMapping("/notifications/test/user-registration/{userId}")
    @Operation(summary = "Simular notificación de nuevo registro")
    public ResponseEntity<Map<String, Object>> testUserRegistrationNotification(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            adminNotificationService.notifyNewUserRegistration(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación de registro enviada para: " + user.getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación de prueba: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando notificación: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Simular primer login de usuario (para pruebas)
     */
    @PostMapping("/notifications/test/first-login/{userId}")
    @Operation(summary = "Simular notificación de primer login")
    public ResponseEntity<Map<String, Object>> testFirstLoginNotification(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            adminNotificationService.notifyFirstUserLogin(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación de primer login enviada para: " + user.getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación de prueba: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando notificación: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Simular nueva solicitud de presupuesto (para pruebas)
     */
    @PostMapping("/notifications/test/budget-request")
    @Operation(summary = "Simular notificación de solicitud de presupuesto")
    public ResponseEntity<Map<String, Object>> testBudgetRequestNotification(
            @RequestParam Long budgetId,
            @RequestParam String clientName,
            @RequestParam String projectName) {
        try {
            adminNotificationService.notifyNewBudgetRequest(budgetId, clientName, projectName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación de presupuesto enviada para: " + projectName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación de prueba: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando notificación: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Simular error del sistema (para pruebas)
     */
    @PostMapping("/notifications/test/system-error")
    @Operation(summary = "Simular notificación de error del sistema")
    public ResponseEntity<Map<String, Object>> testSystemErrorNotification(
            @RequestParam String errorType,
            @RequestParam String errorMessage) {
        try {
            adminNotificationService.notifySystemError(errorType, errorMessage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación de error del sistema enviada");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación de prueba: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando notificación: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Simular actividad inusual (para pruebas)
     */
    @PostMapping("/notifications/test/unusual-activity")
    @Operation(summary = "Simular notificación de actividad inusual")
    public ResponseEntity<Map<String, Object>> testUnusualActivityNotification(
            @RequestParam String activityType,
            @RequestParam Long count) {
        try {
            adminNotificationService.notifyUnusualActivity(activityType, count);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación de actividad inusual enviada");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación de prueba: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error enviando notificación: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ========================================
    // ENDPOINTS DE INFORMACIÓN DEL SISTEMA
    // ========================================

    /**
     * Obtener resumen de estadísticas del sistema
     */
    @GetMapping("/stats/summary")
    @Operation(summary = "Obtener resumen de estadísticas del sistema")
    public ResponseEntity<Map<String, Object>> getSystemStatsSummary() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Estadísticas básicas
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countByStatus("active");
            long totalNotifications = notificationRepository.count();
            long unreadNotifications = notificationRepository.countByTargetRoleAndRead("admin", false);

            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("totalNotifications", totalNotifications);
            stats.put("unreadAdminNotifications", unreadNotifications);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo estadísticas: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de test para verificar funcionamiento del AdminController
     */
    @GetMapping("/test")
    @Operation(summary = "Test del controlador de administrador")
    public ResponseEntity<Map<String, Object>> testAdminController() {
        try {
            Map<String, Object> response = Map.of(
                    "status", "OK",
                    "message", "Controlador de administrador funcionando correctamente",
                    "timestamp", System.currentTimeMillis(),
                    "features", List.of(
                            "Notificaciones automáticas programadas",
                            "Estadísticas diarias/semanales/mensuales",
                            "Alertas de eventos del sistema",
                            "Notificaciones de registro de usuarios",
                            "Alertas de nueva solicitudes de presupuesto"));
            System.out.println("✅ Test del controlador de administrador exitoso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en test del controlador: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
