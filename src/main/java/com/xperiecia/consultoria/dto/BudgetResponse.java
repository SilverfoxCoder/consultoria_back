package com.xperiecia.consultoria.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de presupuesto
 * 
 * Este DTO contiene los datos del presupuesto que se devuelven
 * al frontend, incluyendo información completa del presupuesto.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Data
public class BudgetResponse {

    /**
     * ID único del presupuesto
     */
    private Long id;

    /**
     * Título del proyecto
     */
    private String title;

    /**
     * Descripción detallada del proyecto
     */
    private String description;

    /**
     * Tipo de servicio solicitado
     */
    private String serviceType;

    /**
     * Presupuesto estimado por el cliente
     */
    private Double budget;

    /**
     * Timeline estimado del proyecto
     */
    private String timeline;

    /**
     * Información adicional del proyecto
     */
    private String additionalInfo;

    /**
     * ID del cliente
     */
    private Long clientId;

    /**
     * Nombre del cliente
     */
    private String clientName;

    /**
     * Estado del presupuesto
     */
    private String status;

    /**
     * Estado del presupuesto (display name)
     */
    private String statusDisplay;

    /**
     * Fecha de creación
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de actualización
     */
    private LocalDateTime updatedAt;

    /**
     * Fecha de respuesta (si aplica)
     */
    private LocalDateTime responseDate;

    /**
     * Notas de respuesta
     */
    private String responseNotes;

    /**
     * Presupuesto aprobado (si aplica)
     */
    private Double approvedBudget;

    /**
     * Timeline aprobado (si aplica)
     */
    private String approvedTimeline;
}
