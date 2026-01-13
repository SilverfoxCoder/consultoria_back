package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Notification;
import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.domain.ClientRepository;
import com.xperiecia.consultoria.domain.BudgetRepository;
import com.xperiecia.consultoria.domain.LoginHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class AdminNotificationService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    // ========================================
    // NOTIFICACIONES DE EVENTOS DE USUARIOS
    // ========================================

    /**
     * Notificar a administradores sobre nuevo registro de usuario
     */
    public void notifyNewUserRegistration(User newUser) {
        try {
            String title = "🆕 Nuevo Usuario Registrado";
            String message = String.format(
                    "Un nuevo usuario se ha registrado en el sistema:\n\n" +
                            "👤 Nombre: %s\n" +
                            "📧 Email: %s\n" +
                            "🎯 Rol: %s\n" +
                            "⏰ Fecha: %s\n\n" +
                            "Revisa el panel de administración para más detalles.",
                    newUser.getName(),
                    newUser.getEmail(),
                    newUser.getRole().toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            Notification notification = new Notification(
                    "USER_REGISTRATION",
                    title,
                    message,
                    "medium");
            notification.setTargetRole("admin");
            notification.setRelatedEntityId(newUser.getId());
            notification.setRelatedEntityType("USER");

            notificationService.createNotification(notification);
            System.out.println("🔔 Administradores notificados del nuevo usuario: " + newUser.getName());

        } catch (Exception e) {
            System.err.println("❌ Error notificando nuevo registro: " + e.getMessage());
        }
    }

    /**
     * Notificar login de usuario nuevo (primer acceso)
     */
    public void notifyFirstUserLogin(User user) {
        try {
            String title = "🎉 Primer Acceso de Usuario";
            String message = String.format(
                    "Un usuario ha completado su primer acceso:\n\n" +
                            "👤 Usuario: %s\n" +
                            "📧 Email: %s\n" +
                            "🎯 Rol: %s\n" +
                            "⏰ Primer acceso: %s",
                    user.getName(),
                    user.getEmail(),
                    user.getRole().toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            Notification notification = new Notification(
                    "FIRST_LOGIN",
                    title,
                    message,
                    "low");
            notification.setTargetRole("admin");
            notification.setRelatedEntityId(user.getId());
            notification.setRelatedEntityType("USER");

            notificationService.createNotification(notification);
            System.out.println("🎉 Administradores notificados del primer login: " + user.getName());

        } catch (Exception e) {
            System.err.println("❌ Error notificando primer login: " + e.getMessage());
        }
    }

    /**
     * Notificar nueva solicitud de presupuesto
     */
    public void notifyNewBudgetRequest(Long budgetId, String clientName, String projectName) {
        try {
            String title = "💼 Nueva Solicitud de Presupuesto";
            String message = String.format(
                    "Se ha recibido una nueva solicitud de presupuesto:\n\n" +
                            "👤 Cliente: %s\n" +
                            "📋 Proyecto: %s\n" +
                            "⏰ Fecha: %s\n\n" +
                            "Revisa los detalles en el panel de presupuestos.",
                    clientName,
                    projectName,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            Notification notification = new Notification(
                    "BUDGET_REQUEST",
                    title,
                    message,
                    "high");
            notification.setTargetRole("admin");
            notification.setRelatedEntityId(budgetId);
            notification.setRelatedEntityType("BUDGET");

            notificationService.createNotification(notification);
            System.out.println("💼 Administradores notificados de nueva solicitud de presupuesto");

        } catch (Exception e) {
            System.err.println("❌ Error notificando solicitud de presupuesto: " + e.getMessage());
        }
    }

    // ========================================
    // ESTADÍSTICAS PROGRAMADAS
    // ========================================

    /**
     * Estadísticas diarias - 8:00 AM todos los días
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyStats() {
        try {
            System.out.println("📊 Generando estadísticas diarias...");

            Map<String, Object> stats = getDailyStats();
            String title = "📊 Estadísticas Diarias del Sistema";
            String message = formatDailyStatsMessage(stats);

            Notification notification = new Notification(
                    "DAILY_STATS",
                    title,
                    message,
                    "medium");
            notification.setTargetRole("admin");

            notificationService.createNotification(notification);
            System.out.println("📊 Estadísticas diarias enviadas a administradores");

        } catch (Exception e) {
            System.err.println("❌ Error enviando estadísticas diarias: " + e.getMessage());
        }
    }

    /**
     * Estadísticas semanales - Lunes 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklyStats() {
        try {
            System.out.println("📊 Generando estadísticas semanales...");

            Map<String, Object> stats = getWeeklyStats();
            String title = "📈 Reporte Semanal del Sistema";
            String message = formatWeeklyStatsMessage(stats);

            Notification notification = new Notification(
                    "WEEKLY_STATS",
                    title,
                    message,
                    "medium");
            notification.setTargetRole("admin");

            notificationService.createNotification(notification);
            System.out.println("📈 Estadísticas semanales enviadas a administradores");

        } catch (Exception e) {
            System.err.println("❌ Error enviando estadísticas semanales: " + e.getMessage());
        }
    }

    /**
     * Estadísticas mensuales - Primer día del mes 10:00 AM
     */
    @Scheduled(cron = "0 0 10 1 * ?")
    public void sendMonthlyStats() {
        try {
            System.out.println("📊 Generando estadísticas mensuales...");

            Map<String, Object> stats = getMonthlyStats();
            String title = "📈 Reporte Mensual del Sistema";
            String message = formatMonthlyStatsMessage(stats);

            Notification notification = new Notification(
                    "MONTHLY_STATS",
                    title,
                    message,
                    "high");
            notification.setTargetRole("admin");

            notificationService.createNotification(notification);
            System.out.println("📈 Estadísticas mensuales enviadas a administradores");

        } catch (Exception e) {
            System.err.println("❌ Error enviando estadísticas mensuales: " + e.getMessage());
        }
    }

    // ========================================
    // RECOLECCIÓN DE ESTADÍSTICAS
    // ========================================

    /**
     * Obtener estadísticas diarias
     */
    private Map<String, Object> getDailyStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        // Usuarios registrados hoy
        long newUsersToday = userRepository.countByRegisteredAtBetween(startOfDay, endOfDay);

        // Logins únicos hoy
        long uniqueLoginsToday = loginHistoryRepository.countUniqueUsersByDateRange(startOfDay, endOfDay);

        // Presupuestos creados hoy
        long newBudgetsToday = budgetRepository.countByCreatedAtBetween(startOfDay, endOfDay);

        // Total de usuarios activos
        long totalActiveUsers = userRepository.countByStatus("active");

        stats.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        stats.put("newUsers", newUsersToday);
        stats.put("uniqueLogins", uniqueLoginsToday);
        stats.put("newBudgets", newBudgetsToday);
        stats.put("totalActiveUsers", totalActiveUsers);

        return stats;
    }

    /**
     * Obtener estadísticas semanales
     */
    private Map<String, Object> getWeeklyStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        // Usuarios registrados esta semana
        long newUsersWeek = userRepository.countByRegisteredAtBetween(startOfWeek, endOfWeek);

        // Logins únicos esta semana
        long uniqueLoginsWeek = loginHistoryRepository.countUniqueUsersByDateRange(startOfWeek, endOfWeek);

        // Presupuestos creados esta semana
        long newBudgetsWeek = budgetRepository.countByCreatedAtBetween(startOfWeek, endOfWeek);

        // Clientes activos esta semana
        long activeClientsWeek = clientRepository.countActiveInPeriod(startOfWeek.toLocalDate(), endOfWeek.toLocalDate());

        stats.put("weekStart", startOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        stats.put("weekEnd", endOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        stats.put("newUsers", newUsersWeek);
        stats.put("uniqueLogins", uniqueLoginsWeek);
        stats.put("newBudgets", newBudgetsWeek);
        stats.put("activeClients", activeClientsWeek);

        return stats;
    }

    /**
     * Obtener estadísticas mensuales
     */
    private Map<String, Object> getMonthlyStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0)
                .withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        // Usuarios registrados este mes
        long newUsersMonth = userRepository.countByRegisteredAtBetween(startOfMonth, endOfMonth);

        // Logins únicos este mes
        long uniqueLoginsMonth = loginHistoryRepository.countUniqueUsersByDateRange(startOfMonth, endOfMonth);

        // Presupuestos creados este mes
        long newBudgetsMonth = budgetRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);

        // Total de clientes
        long totalClients = clientRepository.count();

        // Total de usuarios
        long totalUsers = userRepository.count();

        stats.put("month", startOfMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        stats.put("newUsers", newUsersMonth);
        stats.put("uniqueLogins", uniqueLoginsMonth);
        stats.put("newBudgets", newBudgetsMonth);
        stats.put("totalClients", totalClients);
        stats.put("totalUsers", totalUsers);

        return stats;
    }

    // ========================================
    // FORMATEO DE MENSAJES
    // ========================================

    /**
     * Formatear mensaje de estadísticas diarias
     */
    private String formatDailyStatsMessage(Map<String, Object> stats) {
        return String.format(
                "Resumen de actividad del día %s:\n\n" +
                        "👤 Nuevos usuarios: %d\n" +
                        "🔐 Logins únicos: %d\n" +
                        "💼 Nuevos presupuestos: %d\n" +
                        "✅ Total usuarios activos: %d\n\n" +
                        "Mantente al día con la actividad de tu plataforma.",
                stats.get("date"),
                stats.get("newUsers"),
                stats.get("uniqueLogins"),
                stats.get("newBudgets"),
                stats.get("totalActiveUsers"));
    }

    /**
     * Formatear mensaje de estadísticas semanales
     */
    private String formatWeeklyStatsMessage(Map<String, Object> stats) {
        return String.format(
                "📈 Reporte semanal (%s - %s):\n\n" +
                        "👤 Nuevos usuarios: %d\n" +
                        "🔐 Logins únicos: %d\n" +
                        "💼 Nuevos presupuestos: %d\n" +
                        "🏢 Clientes activos: %d\n\n" +
                        "Esta semana ha sido productiva. ¡Sigue así!",
                stats.get("weekStart"),
                stats.get("weekEnd"),
                stats.get("newUsers"),
                stats.get("uniqueLogins"),
                stats.get("newBudgets"),
                stats.get("activeClients"));
    }

    /**
     * Formatear mensaje de estadísticas mensuales
     */
    private String formatMonthlyStatsMessage(Map<String, Object> stats) {
        return String.format(
                "📊 Reporte mensual de %s:\n\n" +
                        "👤 Nuevos usuarios: %d\n" +
                        "🔐 Logins únicos: %d\n" +
                        "💼 Nuevos presupuestos: %d\n" +
                        "🏢 Total clientes: %d\n" +
                        "👥 Total usuarios: %d\n\n" +
                        "Excelente progreso este mes. ¡Continúa creciendo!",
                stats.get("month"),
                stats.get("newUsers"),
                stats.get("uniqueLogins"),
                stats.get("newBudgets"),
                stats.get("totalClients"),
                stats.get("totalUsers"));
    }

    // ========================================
    // NOTIFICACIONES DE EVENTOS CRÍTICOS
    // ========================================

    /**
     * Notificar error crítico del sistema
     */
    public void notifySystemError(String errorType, String errorMessage) {
        try {
            String title = "🚨 Error Crítico del Sistema";
            String message = String.format(
                    "Se ha detectado un error crítico:\n\n" +
                            "🔴 Tipo: %s\n" +
                            "📄 Mensaje: %s\n" +
                            "⏰ Fecha: %s\n\n" +
                            "Revisa los logs del sistema inmediatamente.",
                    errorType,
                    errorMessage,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            Notification notification = new Notification(
                    "SYSTEM_ERROR",
                    title,
                    message,
                    "high");
            notification.setTargetRole("admin");

            notificationService.createNotification(notification);
            System.out.println("🚨 Administradores notificados de error crítico: " + errorType);

        } catch (Exception e) {
            System.err.println("❌ Error notificando error del sistema: " + e.getMessage());
        }
    }

    /**
     * Notificar alta actividad inusual
     */
    public void notifyUnusualActivity(String activityType, long count) {
        try {
            String title = "⚠️ Actividad Inusual Detectada";
            String message = String.format(
                    "Se ha detectado actividad inusual en el sistema:\n\n" +
                            "📊 Tipo de actividad: %s\n" +
                            "🔢 Cantidad: %d\n" +
                            "⏰ Detectado: %s\n\n" +
                            "Considera revisar los logs para más detalles.",
                    activityType,
                    count,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            Notification notification = new Notification(
                    "UNUSUAL_ACTIVITY",
                    title,
                    message,
                    "medium");
            notification.setTargetRole("admin");

            notificationService.createNotification(notification);
            System.out.println("⚠️ Administradores notificados de actividad inusual: " + activityType);

        } catch (Exception e) {
            System.err.println("❌ Error notificando actividad inusual: " + e.getMessage());
        }
    }
}
