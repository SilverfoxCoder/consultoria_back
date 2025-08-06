package com.codethics.consultoria.dto;

import com.codethics.consultoria.domain.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) para la entidad Project
 * 
 * Este DTO se utiliza para transferir datos de proyectos entre la capa de API
 * y el frontend, evitando problemas de serialización JSON circular y
 * proporcionando una estructura de datos optimizada para la presentación.
 * 
 * Características principales:
 * - Evita referencias circulares entre entidades
 * - Incluye nombres de visualización en español
 * - Maneja valores nulos de forma segura
 * - Proporciona datos del cliente sin la entidad completa
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    /** ID único del proyecto */
    private Long id;

    /** Nombre del proyecto */
    private String name;

    /** ID del cliente asociado al proyecto */
    private Long clientId;

    /** Nombre del cliente para mostrar en la interfaz */
    private String clientName;

    /** Estado actual del proyecto (enum como String) */
    private String status;

    /** Nombre de visualización del estado en español */
    private String statusDisplay;

    /** Porcentaje de progreso del proyecto (0-100) */
    private Integer progress;

    /** Fecha de inicio del proyecto */
    private LocalDate startDate;

    /** Fecha de finalización planificada del proyecto */
    private LocalDate endDate;

    /** Presupuesto total asignado al proyecto */
    private BigDecimal budget;

    /** Cantidad gastada hasta el momento */
    private BigDecimal spent;

    /** Prioridad del proyecto (enum como String) */
    private String priority;

    /** Nombre de visualización de la prioridad en español */
    private String priorityDisplay;

    /** Descripción detallada del proyecto */
    private String description;

    /** Indica si la integración con Jira está habilitada */
    private Boolean jiraEnabled;

    /** URL del servidor de Jira */
    private String jiraUrl;

    /** Clave del proyecto en Jira */
    private String jiraProjectKey;

    /** ID del tablero de Jira */
    private String jiraBoardId;

    /** Última sincronización con Jira */
    private LocalDateTime jiraLastSync;

    /** Lista de nombres de miembros del equipo */
    private List<String> team;

    // Campos comentados para futuras implementaciones
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;

    /**
     * Convierte una entidad Project a ProjectDTO
     * 
     * Este método mapea todos los campos de la entidad al DTO,
     * manejando valores nulos de forma segura y evitando referencias
     * circulares al no incluir la entidad completa del cliente.
     * 
     * @param project Entidad Project a convertir
     * @return ProjectDTO con los datos mapeados
     */
    public static ProjectDTO fromEntity(Project project) {
        ProjectDTO dto = new ProjectDTO();

        // Mapear campos básicos
        dto.setId(project.getId());
        dto.setName(project.getName());

        // Mapear datos del cliente de forma segura
        dto.setClientId(project.getClient() != null ? project.getClient().getId() : null);
        dto.setClientName(project.getClient() != null ? project.getClient().getName() : null);

        // Mapear estado con nombre de visualización
        dto.setStatus(project.getStatus() != null ? project.getStatus().name() : null);
        dto.setStatusDisplay(project.getStatus() != null ? project.getStatus().getDisplayName() : null);

        // Mapear campos de progreso y fechas
        dto.setProgress(project.getProgress());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());

        // Mapear campos financieros
        dto.setBudget(project.getBudget());
        dto.setSpent(project.getSpent());

        // Mapear prioridad con nombre de visualización
        dto.setPriority(project.getPriority() != null ? project.getPriority().name() : null);
        dto.setPriorityDisplay(project.getPriority() != null ? project.getPriority().getDisplayName() : null);

        // Mapear descripción
        dto.setDescription(project.getDescription());

        // Mapear configuración de Jira
        dto.setJiraEnabled(project.getJiraEnabled());
        dto.setJiraUrl(project.getJiraUrl());
        dto.setJiraProjectKey(project.getJiraProjectKey());
        dto.setJiraBoardId(project.getJiraBoardId());
        dto.setJiraLastSync(project.getJiraLastSync());

        // Inicializar equipo vacío por defecto
        // En el futuro, se puede poblar con los nombres de los miembros del equipo
        dto.setTeam(new ArrayList<>());

        // Campos de auditoría comentados para futuras implementaciones
        // dto.setCreatedAt(project.getCreatedAt());
        // dto.setUpdatedAt(project.getUpdatedAt());

        return dto;
    }
}