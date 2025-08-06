package com.codethics.consultoria.dto;

import com.codethics.consultoria.domain.InvoiceItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDTO {
    private Long id;
    private Long invoiceId;
    private String invoiceNumber;
    private String clientName;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String type;
    private String typeDisplay;
    private String status;
    private String statusDisplay;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InvoiceItemDTO fromEntity(InvoiceItem entity) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setId(entity.getId());
        dto.setInvoiceId(entity.getInvoice().getId());
        dto.setInvoiceNumber(entity.getInvoice().getNumber());
        dto.setClientName(entity.getInvoice().getClient().getName());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setType(entity.getType().name());
        dto.setTypeDisplay(entity.getType().getDisplayName());
        dto.setStatus(entity.getStatus().name());
        dto.setStatusDisplay(entity.getStatus().getDisplayName());
        dto.setTaxRate(entity.getTaxRate());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
} 