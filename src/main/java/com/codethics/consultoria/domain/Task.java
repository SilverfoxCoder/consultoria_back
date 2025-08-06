package com.codethics.consultoria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status = TaskStatus.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority = TaskPriority.MEDIA;

    @Column(name = "estimated_hours")
    private BigDecimal estimatedHours;

    @Column(name = "actual_hours")
    private BigDecimal actualHours;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums con valores en español
    public enum TaskStatus {
        PENDIENTE("Pendiente"),
        EN_PROGRESO("En Progreso"),
        COMPLETADA("Completada"),
        CANCELADA("Cancelada"),
        PAUSADA("Pausada");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TaskPriority {
        BAJA("Baja"),
        MEDIA("Media"),
        ALTA("Alta"),
        CRITICA("Crítica");

        private final String displayName;

        TaskPriority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}