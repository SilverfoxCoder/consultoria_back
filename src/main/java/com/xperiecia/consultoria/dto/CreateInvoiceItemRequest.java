package com.xperiecia.consultoria.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceItemRequest {
    
    @NotNull(message = "El ID de la factura es obligatorio")
    private Long invoiceId;
    
    @NotBlank(message = "El nombre del ítem es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 999999, message = "La cantidad no puede exceder 999999")
    private Integer quantity;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio unitario no puede exceder 999999.99")
    private BigDecimal unitPrice;
    
    private String type = "SERVICIO";
    
    private String status = "ACTIVO";
    
    @DecimalMin(value = "0.0", message = "La tasa de impuesto no puede ser negativa")
    @DecimalMax(value = "100.0", message = "La tasa de impuesto no puede exceder 100%")
    private BigDecimal taxRate = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "El porcentaje de descuento no puede ser negativa")
    @DecimalMax(value = "100.0", message = "El porcentaje de descuento no puede exceder 100%")
    private BigDecimal discountPercentage = BigDecimal.ZERO;
} 
