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
public class CreateProjectRequest {

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clientId;

    private String status = "PLANIFICACION"; // Valor por defecto en español
    private Integer progress = 0;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "El presupuesto debe ser mayor o igual a 0")
    private BigDecimal budget = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El gasto debe ser mayor o igual a 0")
    private BigDecimal spent = BigDecimal.ZERO;

    private String priority = "MEDIA"; // Valor por defecto en español
    private String description;

    // Campos de Jira
    private Boolean jiraEnabled = false;
    private String jiraUrl;
    private String jiraProjectKey;
    private String jiraBoardId;

    // Lista de IDs de usuarios miembros del equipo
    private java.util.List<Long> teamMemberIds;
}
