package com.xperiecia.consultoria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para gestionar presupuestos de clientes
 * 
 * Esta entidad almacena las solicitudes de presupuesto que los clientes
 * envían a través del frontend, incluyendo detalles del proyecto,
 * presupuesto estimado y timeline.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;

    @Column(name = "budget")
    private Double budget;

    @Column(name = "timeline", length = 100)
    private String timeline;

    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BudgetStatus status = BudgetStatus.PENDIENTE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "response_date")
    private LocalDateTime responseDate;

    @Column(name = "response_notes", columnDefinition = "TEXT")
    private String responseNotes;

    @Column(name = "approved_budget")
    private Double approvedBudget;

    @Column(name = "approved_timeline", length = 100)
    private String approvedTimeline;

    // Enums con valores en español
    public enum BudgetStatus {
        PENDIENTE("Pendiente"),
        EN_REVISION("En Revisión"),
        APROBADO("Aprobado"),
        RECHAZADO("Rechazado"),
        CANCELADO("Cancelado");

        private final String displayName;

        BudgetStatus(String displayName) {
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

    // Métodos de utilidad
    public boolean isPending() {
        return status == BudgetStatus.PENDIENTE;
    }

    public boolean isApproved() {
        return status == BudgetStatus.APROBADO;
    }

    public boolean isRejected() {
        return status == BudgetStatus.RECHAZADO;
    }

    public boolean isUnderReview() {
        return status == BudgetStatus.EN_REVISION;
    }

    public void approve(Double approvedBudget, String approvedTimeline, String responseNotes) {
        System.out.println("=== DEBUG: Budget.approve called ===");
        System.out.println("Approved Budget: " + approvedBudget);
        System.out.println("Approved Timeline: " + approvedTimeline);
        System.out.println("Response Notes: " + responseNotes);

        this.status = BudgetStatus.APROBADO;
        this.approvedBudget = approvedBudget;
        this.approvedTimeline = approvedTimeline;
        this.responseNotes = responseNotes;
        this.responseDate = LocalDateTime.now();

        System.out.println("Budget approved successfully");
    }

    public void reject(String responseNotes) {
        System.out.println("=== DEBUG: Budget.reject called ===");
        System.out.println("Response Notes: " + responseNotes);

        this.status = BudgetStatus.RECHAZADO;
        this.responseNotes = responseNotes;
        this.responseDate = LocalDateTime.now();

        System.out.println("Budget rejected successfully");
    }

    public void setUnderReview() {
        this.status = BudgetStatus.EN_REVISION;
    }
}
