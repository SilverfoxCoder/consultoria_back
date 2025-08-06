package com.codethics.consultoria.dto;

import com.codethics.consultoria.domain.Invoice;
import com.codethics.consultoria.domain.InvoiceItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long id;
    private String number;
    private Long clientId;
    private String clientName;
    private LocalDate issuedAt;
    private LocalDate paidAt;
    private BigDecimal amount;
    private String status;
    private String statusDisplay;
    private String paymentTerms;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InvoiceItemDTO> items;
    private Boolean paid;
    private Boolean draft;
    private Boolean overdue;

    // Constructor from Entity
    public static InvoiceDTO fromEntity(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setNumber(invoice.getNumber());
        dto.setClientId(invoice.getClient() != null ? invoice.getClient().getId() : null);
        dto.setClientName(invoice.getClient() != null ? invoice.getClient().getName() : null);
        dto.setIssuedAt(invoice.getIssuedAt());
        dto.setPaidAt(invoice.getPaidAt());
        dto.setAmount(invoice.getAmount());
        dto.setStatus(invoice.getStatus() != null ? invoice.getStatus().name() : null);
        dto.setStatusDisplay(invoice.getStatus() != null ? invoice.getStatus().getDisplayName() : null);
        dto.setPaymentTerms(invoice.getPaymentTerms());
        dto.setNotes(invoice.getNotes());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());

        // Convertir items a DTOs
        if (invoice.getItems() != null) {
            dto.setItems(invoice.getItems().stream()
                    .map(InvoiceItemDTO::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }

        // Métodos de utilidad
        dto.setPaid(invoice.isPaid());
        dto.setDraft(invoice.isDraft());
        dto.setOverdue(invoice.isOverdue());

        return dto;
    }

    // Método para convertir DTO a Entity
    public Invoice toEntity() {
        Invoice invoice = new Invoice();
        invoice.setId(this.id);
        invoice.setNumber(this.number);
        invoice.setIssuedAt(this.issuedAt);
        invoice.setPaidAt(this.paidAt);
        invoice.setAmount(this.amount);
        if (this.status != null) {
            invoice.setStatus(Invoice.InvoiceStatus.valueOf(this.status));
        }
        invoice.setPaymentTerms(this.paymentTerms);
        invoice.setNotes(this.notes);
        invoice.setCreatedAt(this.createdAt);
        invoice.setUpdatedAt(this.updatedAt);

        // Los items y client se manejarían por separado si es necesario
        return invoice;
    }
}