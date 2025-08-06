package com.codethics.consultoria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus status = ProjectStatus.PLANIFICACION;

    @Column(name = "progress")
    private Integer progress = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget = BigDecimal.ZERO;

    @Column(name = "spent", precision = 15, scale = 2)
    private BigDecimal spent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private ProjectPriority priority = ProjectPriority.MEDIA;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "jira_enabled")
    private Boolean jiraEnabled = false;

    @Column(name = "jira_url")
    private String jiraUrl;

    @Column(name = "jira_project_key")
    private String jiraProjectKey;

    @Column(name = "jira_board_id")
    private String jiraBoardId;

    @Column(name = "jira_last_sync")
    private LocalDateTime jiraLastSync;

    // @Column(name = "created_at")
    // private LocalDateTime createdAt;

    // @Column(name = "updated_at")
    // private LocalDateTime updatedAt;

    // Enums con valores en español
    public enum ProjectStatus {
        PLANIFICACION("Planificación"),
        EN_PROGRESO("En Progreso"),
        COMPLETADO("Completado"),
        CANCELADO("Cancelado"),
        PAUSADO("Pausado");

        private final String displayName;

        ProjectStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ProjectPriority {
        BAJA("Baja"),
        MEDIA("Media"),
        ALTA("Alta"),
        CRITICA("Crítica");

        private final String displayName;

        ProjectPriority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // @PrePersist
    // protected void onCreate() {
    // createdAt = LocalDateTime.now();
    // updatedAt = LocalDateTime.now();
    // }

    // @PreUpdate
    // protected void onUpdate() {
    // updatedAt = LocalDateTime.now();
    // }
}