package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.ProjectRepository;
import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
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
import com.xperiecia.consultoria.domain.ProjectTeam;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectTeamRepository projectTeamRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    @Autowired
    private com.xperiecia.consultoria.domain.ProjectCommentRepository projectCommentRepository;

    // Obtener todos los proyectos
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener proyecto por ID
    public ProjectDTO getProjectById(long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));
        return ProjectDTO.fromEntity(project);
    }

    // Crear nuevo proyecto
    public ProjectDTO createProject(CreateProjectRequest request) {
        // Validaciones
        validateProjectRequest(request);

        // Verificar que el cliente existe (ahora User)
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(
                        () -> new RuntimeException("Cliente (Usuario) no encontrado con ID: " + request.getClientId()));

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

        // Asignar miembros del equipo si se proporcionan IDs
        if (request.getTeamMemberIds() != null && !request.getTeamMemberIds().isEmpty()) {
            for (Long userId : request.getTeamMemberIds()) {
                try {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

                    ProjectTeam teamMember = new ProjectTeam();
                    teamMember.setProject(savedProject);
                    teamMember.setUser(user); // Set the user relationship
                    teamMember.setName(user.getName()); // Usamos el nombre del usuario
                    teamMember.setRole("Consultor"); // Rol por defecto

                    projectTeamRepository.save(teamMember);
                } catch (Exception e) {
                    System.err.println("Error asignando usuario " + userId + " al proyecto: " + e.getMessage());
                }
            }
        }

        return ProjectDTO.fromEntity(savedProject);
    }

    // Actualizar proyecto
    public ProjectDTO updateProject(long id, CreateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));

        validateProjectRequest(request);

        // Verificar que el cliente existe
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(
                        () -> new RuntimeException("Cliente (Usuario) no encontrado con ID: " + request.getClientId()));

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

        if (project.getClient() != null && project.getClient().getId() != null) {
            userRepository.findById(project.getClient().getId().longValue()).ifPresent(project::setClient);
        }
        if (request.getTeamMemberIds() != null) {
            projectTeamRepository.deleteByProjectId(id);
            for (Long userId : request.getTeamMemberIds()) {
                try {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

                    ProjectTeam teamMember = new ProjectTeam();
                    teamMember.setProject(updatedProject);
                    teamMember.setUser(user); // Set the user relationship
                    teamMember.setName(user.getName());
                    teamMember.setRole("Consultor");

                    projectTeamRepository.save(teamMember);
                } catch (Exception e) {
                    System.err.println("Error asignando usuario " + userId + " al proyecto: " + e.getMessage());
                }
            }
        }

        return ProjectDTO.fromEntity(updatedProject);
    }

    // Eliminar proyecto
    public void deleteProject(long id) {
        projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));

        try {
            projectTeamRepository.deleteByProjectId(id);
            taskRepository.deleteByProjectId(id);
            timeEntryRepository.deleteByProjectId(id);
            projectRepository.deleteById(id);
        } catch (Exception e) {
            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException(
                        "No se puede eliminar el proyecto porque tiene dependencias. Elimine primero las dependencias.");
            }
            throw new RuntimeException("Error al eliminar el proyecto: " + e.getMessage());
        }
    }

    // Obtener proyectos por cliente (ahora es User)
    public List<ProjectDTO> getProjectsByClient(long clientId) {
        // Asumiendo que projectRepository.findByClientId ahora busca por el ID del
        // usuario en la columna client_id
        // Si el repositorio usa JPA method names, necesitará ser actualizado a
        // findByClient_Id o similar.
        // Asumiremos que el repositorio será actualizado.
        return projectRepository.findByClient_Id(clientId)
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

    // --- Gestión de Tareas ---

    public List<com.xperiecia.consultoria.dto.TaskDTO> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(com.xperiecia.consultoria.dto.TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Gestión de Comentarios ---

    public List<com.xperiecia.consultoria.dto.ProjectCommentDTO> getProjectComments(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + projectId);
        }
        return projectCommentRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(com.xperiecia.consultoria.dto.ProjectCommentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public com.xperiecia.consultoria.dto.ProjectCommentDTO addProjectComment(Long projectId, String content,
            Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + projectId));

        com.xperiecia.consultoria.domain.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        com.xperiecia.consultoria.domain.ProjectComment comment = new com.xperiecia.consultoria.domain.ProjectComment();
        comment.setProject(project);
        comment.setUser(user);
        comment.setContent(content);

        com.xperiecia.consultoria.domain.ProjectComment savedComment = projectCommentRepository.save(comment);
        return com.xperiecia.consultoria.dto.ProjectCommentDTO.fromEntity(savedComment);
    }

    public com.xperiecia.consultoria.dto.TaskDTO createTaskForProject(Long projectId,
            com.xperiecia.consultoria.dto.CreateTaskRequest request) {
        // Enforce project ID
        request.setProjectId(projectId);

        // This relies on TaskService or we implement logic here.
        // Better to duplicate simple logic to avoid circular dependency if TaskService
        // injects ProjectService (it doesn't seem to, but better safe).
        // Actually, ProjectService injects TaskRepository. We can use it.

        com.xperiecia.consultoria.domain.Task task = new com.xperiecia.consultoria.domain.Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription()); // might be null
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        task.setProject(project);

        if (request.getAssignedToId() != null) {
            com.xperiecia.consultoria.domain.User user = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Usuario asignado no encontrado"));
            task.setAssignedTo(user);
        }

        // Default status
        task.setStatus(com.xperiecia.consultoria.domain.Task.TaskStatus.PENDIENTE);

        com.xperiecia.consultoria.domain.Task savedTask = taskRepository.save(task);
        return com.xperiecia.consultoria.dto.TaskDTO.fromEntity(savedTask);
    }

    // --- Reportes ---

    public com.xperiecia.consultoria.dto.ProjectReportDTO getProjectReport(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + projectId));

        List<com.xperiecia.consultoria.domain.Task> tasks = taskRepository.findByProjectId(projectId);
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
                .filter(t -> com.xperiecia.consultoria.domain.Task.TaskStatus.COMPLETADA.equals(t.getStatus()))
                .count();

        return com.xperiecia.consultoria.dto.ProjectReportDTO.builder()
                .efficiency(project.getEfficiencyScore())
                .hoursLogged(project.getHoursLogged())
                .completedTasks(completedTasks)
                .totalTasks(totalTasks)
                .summary(project.getExecutiveSummary())
                .build();
    }

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
