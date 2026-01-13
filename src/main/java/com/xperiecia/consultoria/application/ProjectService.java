package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.ProjectRepository;
import com.xperiecia.consultoria.domain.Client;
import com.xperiecia.consultoria.domain.ClientRepository;
import com.xperiecia.consultoria.dto.ProjectDTO;
import com.xperiecia.consultoria.dto.CreateProjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.xperiecia.consultoria.domain.ProjectTeamRepository;
import com.xperiecia.consultoria.domain.TaskRepository;
import com.xperiecia.consultoria.domain.TimeEntryRepository;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectTeamRepository projectTeamRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    // Obtener todos los proyectos
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener proyecto por ID
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));
        return ProjectDTO.fromEntity(project);
    }

    // Crear nuevo proyecto
    public ProjectDTO createProject(CreateProjectRequest request) {
        // Validaciones
        validateProjectRequest(request);

        // Verificar que el cliente existe
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + request.getClientId()));

        Project project = new Project();
        project.setName(request.getName());
        project.setClient(client);
        project.setStatus(Project.ProjectStatus.valueOf(request.getStatus()));
        project.setProgress(request.getProgress());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setBudget(request.getBudget());
        project.setSpent(request.getSpent());
        project.setPriority(Project.ProjectPriority.valueOf(request.getPriority()));
        project.setDescription(request.getDescription());
        project.setJiraEnabled(request.getJiraEnabled());
        project.setJiraUrl(request.getJiraUrl());
        project.setJiraProjectKey(request.getJiraProjectKey());
        project.setJiraBoardId(request.getJiraBoardId());

        Project savedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(savedProject);
    }

    // Actualizar proyecto
    public ProjectDTO updateProject(Long id, CreateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));

        validateProjectRequest(request);

        // Verificar que el cliente existe
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + request.getClientId()));

        project.setName(request.getName());
        project.setClient(client);
        project.setStatus(Project.ProjectStatus.valueOf(request.getStatus()));
        project.setProgress(request.getProgress());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setBudget(request.getBudget());
        project.setSpent(request.getSpent());
        project.setPriority(Project.ProjectPriority.valueOf(request.getPriority()));
        project.setDescription(request.getDescription());
        project.setJiraEnabled(request.getJiraEnabled());
        project.setJiraUrl(request.getJiraUrl());
        project.setJiraProjectKey(request.getJiraProjectKey());
        project.setJiraBoardId(request.getJiraBoardId());

        Project updatedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(updatedProject);
    }

    // Eliminar proyecto
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));

        // Eliminar dependencias automáticamente
        try {
            // Eliminar miembros del equipo del proyecto
            projectTeamRepository.deleteByProjectId(id);

            // Eliminar tareas del proyecto
            taskRepository.deleteByProjectId(id);

            // Eliminar entradas de tiempo del proyecto
            timeEntryRepository.deleteByProjectId(id);

            // Finalmente eliminar el proyecto
            projectRepository.deleteById(id);
        } catch (Exception e) {
            // Si hay un error de restricción de clave foránea, proporcionar información más
            // específica
            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException(
                        "No se puede eliminar el proyecto porque tiene dependencias (tareas, entradas de tiempo, etc.). Elimine primero las dependencias.");
            }
            throw new RuntimeException("Error al eliminar el proyecto: " + e.getMessage());
        }
    }

    // Obtener proyectos por cliente
    public List<ProjectDTO> getProjectsByClient(Long clientId) {
        return projectRepository.findByClientId(clientId)
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener proyectos por estado
    public List<ProjectDTO> getProjectsByStatus(String status) {
        Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());
        return projectRepository.findByStatus(projectStatus)
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener proyectos por prioridad
    public List<ProjectDTO> getProjectsByPriority(String priority) {
        Project.ProjectPriority projectPriority = Project.ProjectPriority.valueOf(priority.toUpperCase());
        return projectRepository.findByPriority(projectPriority)
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener proyectos activos
    public List<ProjectDTO> getActiveProjects() {
        return projectRepository.findActiveProjects()
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener proyectos con progreso bajo
    public List<ProjectDTO> getProjectsWithLowProgress() {
        return projectRepository.findProjectsWithLowProgress()
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener proyectos que exceden el presupuesto
    public List<ProjectDTO> getProjectsOverBudget() {
        return projectRepository.findProjectsOverBudget()
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener estadísticas
    public Map<String, Object> getProjectStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", projectRepository.count());
        stats.put("enProgreso", projectRepository.countByStatus(Project.ProjectStatus.EN_PROGRESO));
        stats.put("completados", projectRepository.countByStatus(Project.ProjectStatus.COMPLETADO));
        stats.put("planificacion", projectRepository.countByStatus(Project.ProjectStatus.PLANIFICACION));
        stats.put("cancelados", projectRepository.countByStatus(Project.ProjectStatus.CANCELADO));
        stats.put("pausados", projectRepository.countByStatus(Project.ProjectStatus.PAUSADO));
        stats.put("presupuestoTotal", projectRepository.getTotalBudget());
        stats.put("gastadoTotal", projectRepository.getTotalSpent());

        return stats;
    }

    // Validaciones
    private void validateProjectRequest(CreateProjectRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        if (request.getProgress() < 0 || request.getProgress() > 100) {
            throw new RuntimeException("El progreso debe estar entre 0 y 100");
        }

        if (request.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El presupuesto debe ser mayor o igual a 0");
        }

        if (request.getSpent().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El gasto debe ser mayor o igual a 0");
        }

        // Validar que el status sea válido
        try {
            Project.ProjectStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de proyecto inválido: " + request.getStatus());
        }

        // Validar que la prioridad sea válida
        try {
            Project.ProjectPriority.valueOf(request.getPriority());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Prioridad de proyecto inválida: " + request.getPriority());
        }
    }
}
