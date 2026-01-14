package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Task;
import com.xperiecia.consultoria.domain.TaskRepository;
import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.ProjectRepository;
import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.dto.TaskDTO;
import com.xperiecia.consultoria.dto.CreateTaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // CRUD básico
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + id));
        return TaskDTO.fromEntity(task);
    }

    public TaskDTO createTask(CreateTaskRequest request) {
        validateTaskRequest(request);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setActualHours(request.getActualHours());
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setCompletedDate(request.getCompletedDate());

        // Establecer proyecto
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(
                            () -> new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId()));
            task.setProject(project);
        }

        // Establecer usuario asignado
        if (request.getAssignedToId() != null) {
            User user = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(
                            () -> new RuntimeException("Usuario no encontrado con ID: " + request.getAssignedToId()));
            task.setAssignedTo(user);
        }

        // Establecer status
        if (request.getStatus() != null) {
            task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
        }

        // Establecer priority
        if (request.getPriority() != null) {
            task.setPriority(Task.TaskPriority.valueOf(request.getPriority()));
        }

        Task savedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(savedTask);
    }

    public TaskDTO updateTask(Long id, com.xperiecia.consultoria.dto.UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + id));

        // No full validation needed for partial update

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());
        if (request.getEstimatedHours() != null)
            task.setEstimatedHours(request.getEstimatedHours());
        if (request.getActualHours() != null)
            task.setActualHours(request.getActualHours());
        if (request.getStartDate() != null)
            task.setStartDate(request.getStartDate());
        if (request.getDueDate() != null)
            task.setDueDate(request.getDueDate());
        if (request.getCompletedDate() != null)
            task.setCompletedDate(request.getCompletedDate());

        // Actualizar proyecto
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(
                            () -> new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId()));
            task.setProject(project);
        }

        // Actualizar usuario asignado
        if (request.getAssignedToId() != null) {
            User user = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(
                            () -> new RuntimeException("Usuario no encontrado con ID: " + request.getAssignedToId()));
            task.setAssignedTo(user);
        }

        // Actualizar status
        if (request.getStatus() != null) {
            task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
        }

        // Actualizar priority
        if (request.getPriority() != null) {
            task.setPriority(Task.TaskPriority.valueOf(request.getPriority()));
        }

        Task updatedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Tarea no encontrada con ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Consultas especializadas
    public List<TaskDTO> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedToId(userId).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByStatus(String status) {
        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status);
        return taskRepository.findByStatus(taskStatus).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByPriority(String priority) {
        Task.TaskPriority taskPriority = Task.TaskPriority.valueOf(priority);
        return taskRepository.findByPriority(taskPriority).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getOverdueTasks() {
        return taskRepository.findByDueDateBefore(LocalDate.now()).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByDueDateBetween(startDate, endDate).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByUserAndStatus(Long userId, String status) {
        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status);
        return taskRepository.findTasksByUserAndStatus(userId, taskStatus).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getOverdueTasksByProject(Long projectId) {
        return taskRepository.findOverdueTasksByProject(projectId, LocalDate.now()).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksUnderEstimatedHours() {
        return taskRepository.findTasksUnderEstimatedHours().stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksOverEstimatedHours() {
        return taskRepository.findTasksOverEstimatedHours().stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Estadísticas
    public Long getTaskCountByStatus(String status) {
        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status);
        return taskRepository.countByStatus(taskStatus);
    }

    public Long getTaskCountByProjectAndStatus(Long projectId, String status) {
        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status);
        return taskRepository.countByProjectAndStatus(projectId, taskStatus);
    }

    public Double getAverageActualHoursByProject(Long projectId) {
        return taskRepository.getAverageActualHoursByProject(projectId);
    }

    public Double getTotalActualHoursByProject(Long projectId) {
        return taskRepository.getTotalActualHoursByProject(projectId);
    }

    // Validaciones
    private void validateTaskRequest(CreateTaskRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("El título de la tarea es obligatorio");
        }

        if (request.getProjectId() == null) {
            throw new RuntimeException("El ID del proyecto es obligatorio");
        }

        if (request.getEstimatedHours() != null
                && request.getEstimatedHours().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Las horas estimadas deben ser mayor o igual a 0");
        }

        if (request.getActualHours() != null && request.getActualHours().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Las horas actuales deben ser mayor o igual a 0");
        }

        if (request.getDueDate() != null && request.getStartDate() != null &&
                request.getDueDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("La fecha de vencimiento no puede ser anterior a la fecha de inicio");
        }
    }
}
