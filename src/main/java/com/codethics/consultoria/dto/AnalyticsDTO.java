package com.codethics.consultoria.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    private Long id;
    private Long clientId;
    private String monthPeriod;
    private BigDecimal totalSpent;
    private Integer activeProjects;
    private Integer openTickets;
    private Duration avgResponseTime;
}