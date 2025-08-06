package com.codethics.consultoria.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Table(name = "analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    @Column(name = "month_period", nullable = false)
    private String monthPeriod; // formato 'YYYY-MM'

    @Column(name = "total_spent", precision = 15, scale = 2)
    private BigDecimal totalSpent;

    @Column(name = "active_projects")
    private Integer activeProjects;

    @Column(name = "open_tickets")
    private Integer openTickets;

    @Column(name = "avg_response_time")
    private Duration avgResponseTime;
}