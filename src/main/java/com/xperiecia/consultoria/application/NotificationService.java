package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Notification;
import com.xperiecia.consultoria.domain.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Crear una nueva notificación
     */
    public Notification createNotification(Notification notification) {
        try {
            // Validar que la notificación tenga los campos requeridos
            if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("El título es requerido para crear la notificación");
            }

            if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
                throw new IllegalArgumentException("El mensaje es requerido para crear la notificación");
            }

            // Validar que tenga al menos un target (userId o role)
            if (notification.getTargetUserId() == null &&
                    (notification.getTargetRole() == null || notification.getTargetRole().trim().isEmpty())) {
                throw new IllegalArgumentException("Se requiere userId o role para crear la notificación");
            }

            notification.setCreatedAt(LocalDateTime.now());
            Notification saved = notificationRepository.save(notification);

            System.out.println("📢 Notificación creada: " + saved.getTitle() +
                    " (Target: " + (saved.getTargetUserId() != null ? "User " + saved.getTargetUserId()
                            : "Role " + saved.getTargetRole())
                    + ")");

            // Enviar por WebSocket si está disponible
            sendNotificationViaWebSocket(saved);

            return saved;
        } catch (Exception e) {
            System.err.println("❌ Error creando notificación: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtener notificaciones de un usuario
     */
    public Page<Notification> getUserNotifications(Long userId, String userRole, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserIdOrRole(userId, userRole, pageable);
    }

    /**
     * Marcar una notificación como leída
     */
    public void markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            notificationRepository.save(notification);
            System.out.println("✅ Notificación marcada como leída: " + notificationId);
        }
    }

    /**
     * Marcar todas las notificaciones como leídas
     */
    public void markAllAsRead(Long userId, String userRole) {
        notificationRepository.markAllAsReadByUserIdOrRole(userId, userRole);
        System.out.println("✅ Todas las notificaciones marcadas como leídas para usuario: " + userId);
    }

    /**
     * Eliminar una notificación
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        System.out.println("🗑️ Notificación eliminada: " + notificationId);
    }

    /**
     * Obtener estadísticas de notificaciones
     */
    public Map<String, Object> getNotificationStats(Long userId, String userRole) {
        Long unreadCount = notificationRepository.countUnreadByUserIdOrRole(userId, userRole);
        Page<Notification> allNotifications = notificationRepository.findByUserIdOrRole(userId, userRole,
                Pageable.unpaged());

        Map<String, Object> stats = new HashMap<>();
        stats.put("unread", unreadCount);
        stats.put("total", allNotifications.getTotalElements());

        return stats;
    }

    // ========================================
    // MÉTODOS ESPECÍFICOS PARA PRESUPUESTOS
    // ========================================

    /**
     * Notificar nuevo presupuesto a administradores
     */
    public void notifyNewBudget(Long budgetId, Long clientId, String budgetTitle) {
        try {
            Notification notification = new Notification(
                    "BUDGET_PENDING",
                    "Nuevo Presupuesto Pendiente",
                    "Nuevo presupuesto \"" + budgetTitle + "\" requiere aprobación",
                    "high");
            notification.setTargetRole("admin");
            notification.setRelatedEntityId(budgetId);
            notification.setRelatedEntityType("BUDGET");

            createNotification(notification);
            System.out.println("📊 Notificación de nuevo presupuesto enviada a administradores");
        } catch (Exception e) {
            System.err.println("⚠️ Error creando notificación de nuevo presupuesto: " + e.getMessage());
            // No fallar la creación del presupuesto por error de notificación
        }
    }

    /**
     * Notificar actualización de presupuesto al cliente
     */
    public void notifyBudgetUpdate(Long budgetId, Long clientId, String status, String budgetTitle) {
        String type, title, message, priority;

        switch (status.toUpperCase()) {
            case "APROBADO":
                type = "BUDGET_APPROVED";
                title = "Presupuesto Aprobado";
                message = "Tu presupuesto \"" + budgetTitle + "\" ha sido aprobado";
                priority = "high";
                break;
            case "RECHAZADO":
                type = "BUDGET_REJECTED";
                title = "Presupuesto Rechazado";
                message = "Tu presupuesto \"" + budgetTitle + "\" ha sido rechazado";
                priority = "medium";
                break;
            case "EN_REVISION":
                type = "BUDGET_IN_REVIEW";
                title = "Presupuesto en Revisión";
                message = "Tu presupuesto \"" + budgetTitle + "\" está siendo revisado";
                priority = "medium";
                break;
            default:
                type = "BUDGET_UPDATED";
                title = "Presupuesto Actualizado";
                message = "Tu presupuesto \"" + budgetTitle + "\" ha sido actualizado";
                priority = "low";
        }

        Notification notification = new Notification(type, title, message, priority);
        notification.setTargetUserId(clientId);
        notification.setRelatedEntityId(budgetId);
        notification.setRelatedEntityType("BUDGET");

        createNotification(notification);
        System.out.println("📊 Notificación de actualización de presupuesto enviada al cliente: " + clientId);
    }

    // ========================================
    // MÉTODOS PARA TICKETS (FUTUROS)
    // ========================================

    /**
     * Notificar nuevo ticket a administradores
     */
    public void notifyNewTicket(Long ticketId, Long clientId, String ticketTitle) {
        Notification notification = new Notification(
                "TICKET_NEW",
                "Nuevo Ticket de Soporte",
                "Nuevo ticket \"" + ticketTitle + "\" requiere atención",
                "high");
        notification.setTargetRole("admin");
        notification.setRelatedEntityId(ticketId);
        notification.setRelatedEntityType("ticket");

        createNotification(notification);
        System.out.println("🎫 Notificación de nuevo ticket enviada a administradores");
    }

    /**
     * Notificar actualización de ticket al cliente
     */
    public void notifyTicketUpdate(Long ticketId, Long clientId, String status, String ticketTitle) {
        String type, title, message;

        switch (status.toLowerCase()) {
            case "resolved":
                type = "TICKET_RESOLVED";
                title = "Ticket Resuelto";
                message = "Tu ticket \"" + ticketTitle + "\" ha sido resuelto";
                break;
            case "closed":
                type = "TICKET_CLOSED";
                title = "Ticket Cerrado";
                message = "Tu ticket \"" + ticketTitle + "\" ha sido cerrado";
                break;
            default:
                type = "TICKET_UPDATED";
                title = "Ticket Actualizado";
                message = "Tu ticket \"" + ticketTitle + "\" ha sido actualizado";
        }

        Notification notification = new Notification(type, title, message, "medium");
        notification.setTargetUserId(clientId);
        notification.setRelatedEntityId(ticketId);
        notification.setRelatedEntityType("ticket");

        createNotification(notification);
    }

    // ========================================
    // MÉTODOS PARA PROYECTOS (FUTUROS)
    // ========================================

    /**
     * Notificar actualización de proyecto al cliente
     */
    public void notifyProjectUpdate(Long projectId, Long clientId, String updateType, String projectTitle) {
        String type, title, message;

        switch (updateType) {
            case "milestone":
                type = "PROJECT_MILESTONE";
                title = "Hito del Proyecto Completado";
                message = "Se ha completado un hito en el proyecto \"" + projectTitle + "\"";
                break;
            case "completed":
                type = "PROJECT_COMPLETED";
                title = "Proyecto Completado";
                message = "El proyecto \"" + projectTitle + "\" ha sido completado";
                break;
            case "started":
                type = "PROJECT_STARTED";
                title = "Proyecto Iniciado";
                message = "El proyecto \"" + projectTitle + "\" ha comenzado";
                break;
            default:
                type = "PROJECT_UPDATE";
                title = "Actualización del Proyecto";
                message = "El proyecto \"" + projectTitle + "\" ha sido actualizado";
        }

        Notification notification = new Notification(type, title, message, "medium");
        notification.setTargetUserId(clientId);
        notification.setRelatedEntityId(projectId);
        notification.setRelatedEntityType("project");

        createNotification(notification);
        System.out.println("🏗️ Notificación de proyecto enviada al cliente: " + clientId);
    }

    // ========================================
    // MÉTODOS PARA WEBSOCKET
    // ========================================

    /**
     * Enviar notificación por WebSocket
     */
    private void sendNotificationViaWebSocket(Notification notification) {
        if (messagingTemplate == null) {
            System.out.println("⚠️ WebSocket no configurado, saltando envío en tiempo real");
            return;
        }

        try {
            if (notification.getTargetUserId() != null) {
                // Enviar a usuario específico
                messagingTemplate.convertAndSendToUser(
                        notification.getTargetUserId().toString(),
                        "/queue/notifications",
                        notification);
                System.out
                        .println("📡 Notificación enviada por WebSocket a usuario: " + notification.getTargetUserId());
            } else if (notification.getTargetRole() != null) {
                // Enviar a todos los usuarios con un rol específico
                messagingTemplate.convertAndSend(
                        "/topic/notifications/" + notification.getTargetRole(),
                        notification);
                System.out.println("📡 Notificación enviada por WebSocket a rol: " + notification.getTargetRole());
            }
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación por WebSocket: " + e.getMessage());
        }
    }

    // ========================================
    // MÉTODOS UTILITARIOS
    // ========================================

    /**
     * Crear notificación del sistema
     */
    public void createSystemNotification(String title, String message, String targetRole) {
        Notification notification = new Notification(
                "SYSTEM_ANNOUNCEMENT",
                title,
                message,
                "medium");
        notification.setTargetRole(targetRole);

        createNotification(notification);
        System.out.println("🔔 Notificación del sistema creada: " + title);
    }

    /**
     * Crear notificación de bienvenida para nuevo usuario
     */
    public void createWelcomeNotification(Long userId, String userName) {
        Notification notification = new Notification(
                "WELCOME",
                "¡Bienvenido al Sistema!",
                "Hola " + userName
                        + ", bienvenido a nuestro sistema de gestión. Aquí podrás gestionar tus presupuestos y proyectos.",
                "low");
        notification.setTargetUserId(userId);

        createNotification(notification);
        System.out.println("👋 Notificación de bienvenida enviada a: " + userName);
    }
}
