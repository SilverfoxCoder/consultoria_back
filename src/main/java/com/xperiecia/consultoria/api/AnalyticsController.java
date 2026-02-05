package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Analytics;
import com.xperiecia.consultoria.domain.AnalyticsRepository;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.dto.AnalyticsDTO;
import com.xperiecia.consultoria.dto.DashboardAnalyticsDTO;
import com.xperiecia.consultoria.domain.Budget;
import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.SupportTicket;
import com.xperiecia.consultoria.domain.BudgetRepository;
import com.xperiecia.consultoria.domain.ProjectRepository;
import com.xperiecia.consultoria.domain.SupportTicketRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.YearMonth;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "API para gestión de analíticas y KPIs")
public class AnalyticsController {

        @Autowired
        private AnalyticsRepository analyticsRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BudgetRepository budgetRepository;

        @Autowired
        private ProjectRepository projectRepository;

        @Autowired
        private SupportTicketRepository supportTicketRepository;

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
                Optional<Analytics> analytics = analyticsRepository.findByClient_IdAndMonthPeriod(clientId,
                                monthPeriod);
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
        public ResponseEntity<AnalyticsDTO> updateAnalytics(@PathVariable long id,
                        @RequestBody AnalyticsDTO analyticsDTO) {
                Optional<Analytics> analytics = analyticsRepository.findById(id);
                if (analytics.isPresent()) {
                        Analytics updatedAnalytics = analytics.get();
                        updatedAnalytics.setMonthPeriod(analyticsDTO.getMonthPeriod());
                        updatedAnalytics.setTotalSpent(analyticsDTO.getTotalSpent());
                        updatedAnalytics.setActiveProjects(analyticsDTO.getActiveProjects());
                        updatedAnalytics.setOpenTickets(analyticsDTO.getOpenTickets());
                        updatedAnalytics.setAvgResponseTime(analyticsDTO.getAvgResponseTime());

                        if (analyticsDTO.getClientId() != null) {
                                userRepository.findById(analyticsDTO.getClientId().longValue())
                                                .ifPresent(updatedAnalytics::setClient);
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

        @GetMapping("/dashboard/{clientId}")
        @Operation(summary = "Obtener analíticas en tiempo real para dashboard")
        public ResponseEntity<DashboardAnalyticsDTO> getDashboardAnalytics(@PathVariable Long clientId) {
                DashboardAnalyticsDTO dashboard = new DashboardAnalyticsDTO();

                // 1. Calculate Total Spent (Approved Budgets)
                BigDecimal totalSpent = budgetRepository.findByClient_Id(clientId).stream()
                                .filter(b -> b.getStatus() == com.xperiecia.consultoria.domain.Budget.BudgetStatus.APROBADO)
                                .map(b -> b.getApprovedBudget() != null ? BigDecimal.valueOf(b.getApprovedBudget())
                                                : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                dashboard.setTotalSpent(totalSpent);

                // 2. Count Pending Budgets
                int pendingBudgets = (int) budgetRepository.findByClient_Id(clientId).stream()
                                .filter(b -> b.getStatus() == com.xperiecia.consultoria.domain.Budget.BudgetStatus.PENDIENTE)
                                .count();
                dashboard.setPendingBudgets(pendingBudgets);

                // 3. Count Active Projects
                int activeProjects = (int) projectRepository.findByClient_Id(clientId).stream()
                                .filter(p -> p.getStatus() == com.xperiecia.consultoria.domain.Project.ProjectStatus.EN_PROGRESO
                                                || p.getStatus() == com.xperiecia.consultoria.domain.Project.ProjectStatus.PLANIFICACION)
                                .count();
                dashboard.setActiveProjects(activeProjects);

                // 4. Count Open Tickets
                // SupportTicket uses String for status based on repository definition
                int openTickets = (int) supportTicketRepository.findByClient_Id(clientId).stream()
                                .filter(t -> "OPEN".equalsIgnoreCase(t.getStatus())
                                                || "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                                .count();
                dashboard.setOpenTickets(openTickets);

                // 5. Monthly Spending (Mock for now, or aggregate by Budget date)
                List<DashboardAnalyticsDTO.MonthlySpendingDTO> monthlySpending = new ArrayList<>();
                YearMonth currentMonth = YearMonth.now();
                for (int i = 5; i >= 0; i--) {
                        YearMonth month = currentMonth.minusMonths(i);
                        String monthLabel = month.getMonth().name().substring(0, 3) + " " + month.getYear();
                        BigDecimal monthlyAmount = totalSpent.divide(BigDecimal.valueOf(6), 2,
                                        java.math.RoundingMode.HALF_UP);
                        monthlySpending.add(new DashboardAnalyticsDTO.MonthlySpendingDTO(monthLabel, monthlyAmount));
                }
                dashboard.setMonthlySpending(monthlySpending);

                // 6. Service Breakdown (Mock or aggregate by Project Type)
                List<DashboardAnalyticsDTO.ServiceBreakdownDTO> serviceBreakdown = new ArrayList<>();
                serviceBreakdown.add(new DashboardAnalyticsDTO.ServiceBreakdownDTO("Consultoría",
                                totalSpent.multiply(new BigDecimal("0.4")), 40.0));
                serviceBreakdown.add(new DashboardAnalyticsDTO.ServiceBreakdownDTO("Desarrollo",
                                totalSpent.multiply(new BigDecimal("0.3")), 30.0));
                serviceBreakdown.add(new DashboardAnalyticsDTO.ServiceBreakdownDTO("Soporte",
                                totalSpent.multiply(new BigDecimal("0.2")), 20.0));
                serviceBreakdown.add(new DashboardAnalyticsDTO.ServiceBreakdownDTO("Infraestructura",
                                totalSpent.multiply(new BigDecimal("0.1")), 10.0));
                dashboard.setServiceBreakdown(serviceBreakdown);

                // 7. Recent Activity (Combine Projects and Tickets)
                List<DashboardAnalyticsDTO.RecentActivityDTO> activity = new ArrayList<>();

                // Add recent projects
                projectRepository.findByClient_Id(clientId).stream()
                                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                                .limit(3)
                                .forEach(p -> activity.add(new DashboardAnalyticsDTO.RecentActivityDTO(
                                                "P-" + p.getId(), "PROJECT", "Proyecto: " + p.getName(),
                                                "Estado: " + p.getStatus().getDisplayName(),
                                                LocalDate.now().toString(), "10:00")));

                // Add recent tickets
                supportTicketRepository.findByClient_Id(clientId).stream()
                                .sorted((t1, t2) -> t2.getId().compareTo(t1.getId()))
                                .limit(3)
                                .forEach(t -> activity.add(new DashboardAnalyticsDTO.RecentActivityDTO(
                                                "T-" + t.getId(), "TICKET", "Ticket: " + t.getTitle(),
                                                "Estado: " + t.getStatus(),
                                                LocalDate.now().toString(), "11:00")));

                dashboard.setRecentActivity(activity);

                return ResponseEntity.ok(dashboard);
        }
}
