package com.xperiecia.consultoria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, unique = true, length = 50)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "issued_at")
    private LocalDate issuedAt;

    @Column(name = "paid_at")
    private LocalDate paidAt;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status = InvoiceStatus.BORRADOR;

    @Column(name = "payment_terms", length = 255)
    private String paymentTerms;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();

    // Enums con valores en español
    public enum InvoiceStatus {
        BORRADOR("Borrador"),
        ENVIADA("Enviada"),
        PAGADA("Pagada"),
        VENCIDA("Vencida"),
        CANCELADA("Cancelada");

        private final String displayName;

        InvoiceStatus(String displayName) {
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
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    // Método para calcular totales
    public void calculateTotals() {
        // Calcular total de los ítems
        BigDecimal itemsTotal = items.stream()
                .filter(item -> item.getStatus() == InvoiceItem.ItemStatus.ACTIVO)
                .map(InvoiceItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.amount = itemsTotal;
    }

    // Métodos de utilidad
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
        calculateTotals();
    }

    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
        calculateTotals();
    }

    public boolean isOverdue() {
        // Como no tenemos due_date, consideramos vencida si no está pagada y tiene más
        // de 30 días
        if (status == InvoiceStatus.PAGADA || status == InvoiceStatus.CANCELADA) {
            return false;
        }
        if (issuedAt == null) {
            return false;
        }
        return LocalDate.now().isAfter(issuedAt.plusDays(30));
    }

    public boolean isPaid() {
        return status == InvoiceStatus.PAGADA;
    }

    public boolean isDraft() {
        return status == InvoiceStatus.BORRADOR;
    }
}
