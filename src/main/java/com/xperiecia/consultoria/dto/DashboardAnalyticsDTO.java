package com.xperiecia.consultoria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsDTO {
    private BigDecimal totalSpent;
    private int activeProjects;
    private int openTickets;
    private int pendingBudgets;

    // Charts Data
    private List<MonthlySpendingDTO> monthlySpending;
    private List<ServiceBreakdownDTO> serviceBreakdown;

    // Lists
    private List<RecentActivityDTO> recentActivity;

    @Data
    @AllArgsConstructor
    public static class MonthlySpendingDTO {
        private String month;
        private BigDecimal amount;
    }

    @Data
    @AllArgsConstructor
    public static class ServiceBreakdownDTO {
        private String serviceName;
        private BigDecimal amount;
        private double percentage;
    }

    @Data
    @AllArgsConstructor
    public static class RecentActivityDTO {
        private String id;
        private String type; // PROJECT, TICKET, BUDGET
        private String title;
        private String description;
        private String date;
        private String time;
    }
}
