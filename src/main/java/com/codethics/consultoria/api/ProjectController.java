package com.codethics.consultoria.api;

import com.codethics.consultoria.application.ProjectService;
import com.codethics.consultoria.dto.ProjectDTO;
import com.codethics.consultoria.dto.CreateProjectRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de proyectos
 * 
 * Este controlador proporciona endpoints para realizar operaciones CRUD
 * sobre proyectos, incluyendo búsquedas por diferentes criterios
 * y estadísticas de proyectos.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "API para gestión de proyectos")
public class ProjectController {

    /**
     * Servicio de proyectos que maneja la lógica de negocio
     */
    @Autowired
    private ProjectService projectService;

    /**
     * Obtiene todos los proyectos del sistema
     * 
     * @return Lista de proyectos en formato DTO
     */
    @GetMapping
    @Operation(summary = "Obtener todos los proyectos")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Obtiene un proyecto específico por su ID
     * 
     * @param id ID del proyecto a buscar
     * @return Proyecto encontrado o 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un proyecto por ID")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        try {
            ProjectDTO project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            // Si el proyecto no existe, retornar 404
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene todos los proyectos de un cliente específico
     * 
     * @param clientId ID del cliente
     * @return Lista de proyectos del cliente
     */
    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener proyectos por cliente")
    public ResponseEntity<List<ProjectDTO>> getProjectsByClient(@PathVariable Long clientId) {
        List<ProjectDTO> projects = projectService.getProjectsByClient(clientId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Obtiene proyectos filtrados por estado
     * Estados válidos: PLANIFICACION, EN_PROGRESO, COMPLETADO, CANCELADO, PAUSADO
     * 
     * @param status Estado de los proyectos a buscar
     * @return Lista de proyectos con el estado especificado
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener proyectos por estado")
    public ResponseEntity<List<ProjectDTO>> getProjectsByStatus(@PathVariable String status) {
        try {
            List<ProjectDTO> projects = projectService.getProjectsByStatus(status);
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            // Estado no válido
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene proyectos filtrados por prioridad
     * Prioridades válidas: BAJA, MEDIA, ALTA, CRITICA
     * 
     * @param priority Prioridad de los proyectos a buscar
     * @return Lista de proyectos con la prioridad especificada
     */
    @GetMapping("/priority/{priority}")
    @Operation(summary = "Obtener proyectos por prioridad")
    public ResponseEntity<List<ProjectDTO>> getProjectsByPriority(@PathVariable String priority) {
        try {
            List<ProjectDTO> projects = projectService.getProjectsByPriority(priority);
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            // Prioridad no válida
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene proyectos que están actualmente activos
     * (EN_PROGRESO o PLANIFICACION)
     * 
     * @return Lista de proyectos activos
     */
    @GetMapping("/active")
    @Operation(summary = "Obtener proyectos activos")
    public ResponseEntity<List<ProjectDTO>> getActiveProjects() {
        List<ProjectDTO> projects = projectService.getActiveProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Obtiene proyectos con progreso bajo (menos del 25%)
     * 
     * @return Lista de proyectos con progreso bajo
     */
    @GetMapping("/low-progress")
    @Operation(summary = "Obtener proyectos con progreso bajo")
    public ResponseEntity<List<ProjectDTO>> getProjectsWithLowProgress() {
        List<ProjectDTO> projects = projectService.getProjectsWithLowProgress();
        return ResponseEntity.ok(projects);
    }

    /**
     * Obtiene proyectos que han excedido su presupuesto
     * (gastado > presupuesto)
     * 
     * @return Lista de proyectos que exceden el presupuesto
     */
    @GetMapping("/over-budget")
    @Operation(summary = "Obtener proyectos que exceden el presupuesto")
    public ResponseEntity<List<ProjectDTO>> getProjectsOverBudget() {
        List<ProjectDTO> projects = projectService.getProjectsOverBudget();
        return ResponseEntity.ok(projects);
    }

    /**
     * Crea un nuevo proyecto en el sistema
     * 
     * @param request Datos del proyecto a crear
     * @return Proyecto creado o 400 si hay errores de validación
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo proyecto")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody CreateProjectRequest request) {
        try {
            ProjectDTO createdProject = projectService.createProject(request);
            return ResponseEntity.ok(createdProject);
        } catch (RuntimeException e) {
            // Error en la creación (cliente no existe, datos inválidos, etc.)
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un proyecto existente
     * 
     * @param id      ID del proyecto a actualizar
     * @param request Nuevos datos del proyecto
     * @return Proyecto actualizado o 404 si no existe
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un proyecto")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id,
            @Valid @RequestBody CreateProjectRequest request) {
        try {
            ProjectDTO updatedProject = projectService.updateProject(id, request);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            // Proyecto no encontrado
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un proyecto del sistema
     * 
     * @param id ID del proyecto a eliminar
     * @return Respuesta con mensaje de confirmación o error
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un proyecto")
    public ResponseEntity<Map<String, Object>> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            // Respuesta exitosa con detalles
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Proyecto eliminado correctamente");
            response.put("id", id);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Error al eliminar (proyecto no existe, tiene dependencias, etc.)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al eliminar el proyecto: " + e.getMessage());
            response.put("error", e.getMessage());
            response.put("success", false);
            response.put("id", id);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            // Error inesperado
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error inesperado al eliminar el proyecto");
            response.put("error", e.getMessage());
            response.put("success", false);
            response.put("id", id);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Obtiene estadísticas generales de proyectos
     * Incluye conteos por estado, prioridad, presupuesto, etc.
     * 
     * @return Mapa con estadísticas de proyectos
     */
    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de proyectos")
    public ResponseEntity<Map<String, Object>> getProjectStats() {
        Map<String, Object> stats = projectService.getProjectStats();
        return ResponseEntity.ok(stats);
    }
}