package com.codethics.consultoria.dto;

import lombok.Data;

/**
 * DTO para solicitudes de presupuesto
 * 
 * Este DTO contiene los datos necesarios para crear
 * una nueva solicitud de presupuesto desde el frontend.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Data
public class BudgetRequest {

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
     * ID del cliente que solicita el presupuesto
     */
    private Long clientId;
}