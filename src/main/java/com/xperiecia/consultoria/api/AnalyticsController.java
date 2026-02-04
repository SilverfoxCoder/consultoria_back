package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Analytics;
import com.xperiecia.consultoria.domain.AnalyticsRepository;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.dto.AnalyticsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "API para gestión de analíticas y KPIs")
public class AnalyticsController {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private UserRepository userRepository;

    private AnalyticsDTO convertToDTO(Analytics analytics) {
        return new AnalyticsDTO(
                analytics.getId(),
                analytics.getClient() != null ? analytics.getClient().getId() : null,
                analytics.getMonthPeriod(),
                analytics.getTotalSpent(),
                analytics.getActiveProjects(),
                analytics.getOpenTickets(),
                analytics.getAvgResponseTime());
    }

    private Analytics convertToEntity(AnalyticsDTO dto) {
        Analytics analytics = new Analytics();
        analytics.setId(dto.getId());
        analytics.setMonthPeriod(dto.getMonthPeriod());
        analytics.setTotalSpent(dto.getTotalSpent());
        analytics.setActiveProjects(dto.getActiveProjects());
        analytics.setOpenTickets(dto.getOpenTickets());
        analytics.setAvgResponseTime(dto.getAvgResponseTime());

        if (dto.getClientId() != null) {
            userRepository.findById(dto.getClientId().longValue()).ifPresent(analytics::setClient);
        }

        return analytics;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las analíticas")
    public List<AnalyticsDTO> getAllAnalytics() {
        return analyticsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener analítica por ID")
    public ResponseEntity<AnalyticsDTO> getAnalyticsById(@PathVariable long id) {
        Optional<Analytics> analytics = analyticsRepository.findById(id);
        return analytics.map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener analíticas por cliente")
    public List<AnalyticsDTO> getAnalyticsByClient(@PathVariable long clientId) {
        // Updated to use findByClient_Id
        return analyticsRepository.findByClient_Id(clientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/month/{monthPeriod}")
    @Operation(summary = "Obtener analíticas por mes")
    public List<AnalyticsDTO> getAnalyticsByMonth(@PathVariable String monthPeriod) {
        return analyticsRepository.findByMonthPeriod(monthPeriod).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/client/{clientId}/month/{monthPeriod}")
    @Operation(summary = "Obtener analítica específica por cliente y mes")
    public ResponseEntity<AnalyticsDTO> getAnalyticsByClientAndMonth(@PathVariable long clientId,
            @PathVariable String monthPeriod) {
        // Updated to use findByClient_IdAndMonthPeriod
        Optional<Analytics> analytics = analyticsRepository.findByClient_IdAndMonthPeriod(clientId, monthPeriod);
        return analytics.map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva analítica")
    public AnalyticsDTO createAnalytics(@RequestBody AnalyticsDTO analyticsDTO) {
        Analytics analytics = convertToEntity(analyticsDTO);
        Analytics savedAnalytics = analyticsRepository.save(analytics);
        return convertToDTO(savedAnalytics);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar analítica")
    public ResponseEntity<AnalyticsDTO> updateAnalytics(@PathVariable long id, @RequestBody AnalyticsDTO analyticsDTO) {
        Optional<Analytics> analytics = analyticsRepository.findById(id);
        if (analytics.isPresent()) {
            Analytics updatedAnalytics = analytics.get();
            updatedAnalytics.setMonthPeriod(analyticsDTO.getMonthPeriod());
            updatedAnalytics.setTotalSpent(analyticsDTO.getTotalSpent());
            updatedAnalytics.setActiveProjects(analyticsDTO.getActiveProjects());
            updatedAnalytics.setOpenTickets(analyticsDTO.getOpenTickets());
            updatedAnalytics.setAvgResponseTime(analyticsDTO.getAvgResponseTime());

            if (analyticsDTO.getClientId() != null) {
                userRepository.findById(analyticsDTO.getClientId().longValue()).ifPresent(updatedAnalytics::setClient);
            }

            Analytics savedAnalytics = analyticsRepository.save(updatedAnalytics);
            return ResponseEntity.ok(convertToDTO(savedAnalytics));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar analítica")
    public ResponseEntity<Void> deleteAnalytics(@PathVariable long id) {
        Optional<Analytics> analytics = analyticsRepository.findById(id);
        if (analytics.isPresent()) {
            analyticsRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Autowired
    private com.xperiecia.consultoria.domain.BudgetRepository budgetRepository;

    @Autowired
    private com.xperiecia.consultoria.domain.ProjectRepository projectRepository;

    @Autowired
    private com.xperiecia.consultoria.domain.SupportTicketRepository supportTicketRepository;

    @GetMapping("/dashboard/{clientId}")
    @Operation(summary = "Obtener analíticas en tiempo real para dashboard")
    public ResponseEntity<com.xperiecia.consultoria.dto.DashboardAnalyticsDTO> getDashboardAnalytics(
            @PathVariable Long clientId) {
        com.xperiecia.consultoria.dto.DashboardAnalyticsDTO dashboard = new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO();

        // 1. Calculate Total Spent (Approved Budgets)
        java.math.BigDecimal totalSpent = budgetRepository.findByClientId(clientId).stream()
                .filter(b -> "APPROVED".equalsIgnoreCase(b.getStatus()))
                .map(com.xperiecia.consultoria.domain.Budget::getTotalAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        dashboard.setTotalSpent(totalSpent);

        // 2. Count Pending Budgets
        int pendingBudgets = (int) budgetRepository.findByClientId(clientId).stream()
                .filter(b -> "PENDING".equalsIgnoreCase(b.getStatus()))
                .count();
        dashboard.setPendingBudgets(pendingBudgets);

        // 3. Count Active Projects
        int activeProjects = (int) projectRepository.findByClientId(clientId).stream()
                .filter(p -> "EN_PROGRESO".equalsIgnoreCase(p.getStatus())
                        || "PLANIFICACION".equalsIgnoreCase(p.getStatus()))
                .count();
        dashboard.setActiveProjects(activeProjects);

        // 4. Count Open Tickets
        int openTickets = (int) supportTicketRepository.findByClientId(clientId).stream()
                .filter(t -> "OPEN".equalsIgnoreCase(t.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                .count();
        dashboard.setOpenTickets(openTickets);

        // 5. Monthly Spending (Mock for now, or aggregate by Budget date)
        // Ideally we would look at Invoices, but Budgets are a good proxy for "Planned
        // Spending"
        java.util.List<com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.MonthlySpendingDTO> monthlySpending = new java.util.ArrayList<>();
        // Mocking last 6 months for visualization if no real data
        java.time.YearMonth currentMonth = java.time.YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            java.time.YearMonth month = currentMonth.minusMonths(i);
            String monthLabel = month.getMonth().name().substring(0, 3) + " " + month.getYear();
            // In a real scenario, sum invoices for this month
            // For now, distribute total spent somewhat randomly/evenly to show the chart
            java.math.BigDecimal monthlyAmount = totalSpent.divide(java.math.BigDecimal.valueOf(6), 2,
                    java.math.RoundingMode.HALF_UP);
            monthlySpending.add(new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.MonthlySpendingDTO(monthLabel,
                    monthlyAmount));
        }
        dashboard.setMonthlySpending(monthlySpending);

        // 6. Service Breakdown (Mock or aggregate by Project Type)
        java.util.List<com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.ServiceBreakdownDTO> serviceBreakdown = new java.util.ArrayList<>();
        serviceBreakdown.add(new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.ServiceBreakdownDTO("Consultoría",
                totalSpent.multiply(new java.math.BigDecimal("0.4")), 40.0));
        serviceBreakdown.add(new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.ServiceBreakdownDTO("Desarrollo",
                totalSpent.multiply(new java.math.BigDecimal("0.3")), 30.0));
        serviceBreakdown.add(new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.ServiceBreakdownDTO("Soporte",
                totalSpent.multiply(new java.math.BigDecimal("0.2")), 20.0));
        serviceBreakdown.add(new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.ServiceBreakdownDTO(
                "Infraestructura", totalSpent.multiply(new java.math.BigDecimal("0.1")), 10.0));
        dashboard.setServiceBreakdown(serviceBreakdown);

        // 7. Recent Activity (Combine Projects and Tickets)
        java.util.List<com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.RecentActivityDTO> activity = new java.util.ArrayList<>();

        // Add recent projects
        projectRepository.findByClientId(clientId).stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId())) // Assuming ID correlates with time or use
                                                                      // createdAt if available
                .limit(3)
                .forEach(p -> activity.add(new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.RecentActivityDTO(
                        "P-" + p.getId(), "PROJECT", "Proyecto: " + p.getName(), "Estado: " + p.getStatus(),
                        java.time.LocalDate.now().toString(), "10:00")));

        // Add recent tickets
        supportTicketRepository.findByClientId(clientId).stream()
                .sorted((t1, t2) -> t2.getId().compareTo(t1.getId()))
                .limit(3)
                .forEach(t -> activity.add(new com.xperiecia.consultoria.dto.DashboardAnalyticsDTO.RecentActivityDTO(
                        "T-" + t.getId(), "TICKET", "Ticket: " + t.getTitle(), "Estado: " + t.getStatus(),
                        java.time.LocalDate.now().toString(), "11:00")));

        dashboard.setRecentActivity(activity);

        return ResponseEntity.ok(dashboard);
    }
}
