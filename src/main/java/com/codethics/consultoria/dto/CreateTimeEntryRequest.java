package com.codethics.consultoria.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeEntryRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    private Long projectId;

    private Long taskId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal durationHours;

    private String description;

    private String status = "ACTIVO"; // Valor por defecto

    private Boolean billable = true; // Valor por defecto

    @DecimalMin(value = "0.0", message = "La tarifa de facturación debe ser mayor o igual a 0")
    private BigDecimal billingRate;

    private BigDecimal totalAmount;
} 