package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para el dashboard del cliente
 * 
 * Este controlador proporciona endpoints específicos para el dashboard
 * del frontend, devolviendo datos en el formato exacto que necesita.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "API para datos del dashboard")
public class DashboardController {

    @Autowired
    private ProjectRepository projectRepository;

    // @Autowired
    // private ClientRepository clientRepository; // Removed

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Obtener datos del dashboard
     * 
     * @return Datos completos del dashboard
     */
    @GetMapping("/data")
    @Operation(summary = "Obtener datos del dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            Map<String, Object> dashboardData = new HashMap<>();

            // Métricas principales
            Map<String, Object> metrics = new HashMap<>();

            // Proyectos
            long totalProjects = projectRepository.count();
            long activeProjects = projectRepository.countByStatus(Project.ProjectStatus.EN_PROGRESO);
            long planningProjects = projectRepository.countByStatus(Project.ProjectStatus.PLANIFICACION);
            long completedProjects = projectRepository.countByStatus(Project.ProjectStatus.COMPLETADO);

            Map<String, Object> projects = new HashMap<>();
            projects.put("total", totalProjects);
            projects.put("active", activeProjects);
            projects.put("planning", planningProjects);
            projects.put("completed", completedProjects);
            metrics.put("projects", projects);

            // Clientes (migrado a UserRepository)
            long totalClients = userRepository.countByRole("CLIENT");
            long activeClients = userRepository.countByRoleAndStatus("CLIENT", "Activo");
            long prospectClients = userRepository.countByRoleAndStatus("CLIENT", "Prospecto");

            Map<String, Object> clients = new HashMap<>();
            clients.put("total", totalClients);
            clients.put("active", activeClients);
            clients.put("prospect", prospectClients);
            metrics.put("clients", clients);

            // Usuarios
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.count(); // TODO: filtrar por status si es necesario

            Map<String, Object> users = new HashMap<>();
            users.put("total", totalUsers);
            users.put("active", activeUsers);
            metrics.put("users", users);

            // Facturas
            long totalInvoices = invoiceRepository.count();
            long draftInvoices = invoiceRepository.countByStatus(Invoice.InvoiceStatus.BORRADOR);
            long sentInvoices = invoiceRepository.countByStatus(Invoice.InvoiceStatus.ENVIADA);
            long paidInvoices = invoiceRepository.countByStatus(Invoice.InvoiceStatus.PAGADA);

            Map<String, Object> invoices = new HashMap<>();
            invoices.put("total", totalInvoices);
            invoices.put("draft", draftInvoices);
            invoices.put("sent", sentInvoices);
            invoices.put("paid", paidInvoices);
            metrics.put("invoices", invoices);

            // Tareas
            long totalTasks = taskRepository.count();
            long pendingTasks = taskRepository.countByStatus(Task.TaskStatus.PENDIENTE);
            long inProgressTasks = taskRepository.countByStatus(Task.TaskStatus.EN_PROGRESO);
            long completedTasks = taskRepository.countByStatus(Task.TaskStatus.COMPLETADA);

            Map<String, Object> tasks = new HashMap<>();
            tasks.put("total", totalTasks);
            tasks.put("pending", pendingTasks);
            tasks.put("inProgress", inProgressTasks);
            tasks.put("completed", completedTasks);
            metrics.put("tasks", tasks);

            dashboardData.put("metrics", metrics);
            dashboardData.put("timestamp", LocalDateTime.now().toString());
            dashboardData.put("status", "success");

            return ResponseEntity.ok(dashboardData);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error obteniendo datos del dashboard: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener resumen rápido del dashboard
     * 
     * @return Resumen de métricas principales
     */
    @GetMapping("/summary")
    @Operation(summary = "Obtener resumen del dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            Map<String, Object> summary = new HashMap<>();

            // Contadores principales
            summary.put("totalProjects", projectRepository.count());
            summary.put("activeProjects", projectRepository.countByStatus(Project.ProjectStatus.EN_PROGRESO));
            summary.put("totalClients", userRepository.countByRole("CLIENT"));
            summary.put("activeClients", userRepository.countByRoleAndStatus("CLIENT", "Activo"));
            summary.put("totalUsers", userRepository.count());
            summary.put("totalInvoices", invoiceRepository.count());
            summary.put("totalTasks", taskRepository.count());

            summary.put("timestamp", LocalDateTime.now().toString());
            summary.put("status", "success");

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error obteniendo resumen: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
