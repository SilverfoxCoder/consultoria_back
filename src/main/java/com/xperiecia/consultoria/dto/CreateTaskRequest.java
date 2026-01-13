package com.xperiecia.consultoria.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "El título de la tarea es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String title;

    private String description;

    @NotNull(message = "El ID del proyecto es obligatorio")
    private Long projectId;

    private Long assignedToId;

    private String status = "PENDIENTE"; // Valor por defecto

    private String priority = "MEDIA"; // Valor por defecto

    @DecimalMin(value = "0.0", message = "Las horas estimadas deben ser mayor o igual a 0")
    private BigDecimal estimatedHours;

    @DecimalMin(value = "0.0", message = "Las horas actuales deben ser mayor o igual a 0")
    private BigDecimal actualHours;

    private LocalDate startDate;

    private LocalDate dueDate;

    private LocalDate completedDate;
}
