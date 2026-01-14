package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.application.TaskService;
import com.xperiecia.consultoria.dto.TaskDTO;
import com.xperiecia.consultoria.dto.CreateTaskRequest;
import com.xperiecia.consultoria.domain.Task;
import com.xperiecia.consultoria.domain.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    // CRUD básico
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskDTO createdTask = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id,
            @RequestBody com.xperiecia.consultoria.dto.UpdateTaskRequest request) {
        TaskDTO updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            try {
                taskRepository.deleteById(id);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Tarea eliminada correctamente");
                response.put("id", id);
                response.put("success", true);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Error al eliminar la tarea: " + e.getMessage());
                response.put("error", e.getMessage());
                response.put("success", false);
                response.put("id", id);
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tarea no encontrada");
            response.put("success", false);
            response.put("id", id);
            return ResponseEntity.notFound().build();
        }
    }

    // Consultas especializadas
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksByUser(@PathVariable Long userId) {
        List<TaskDTO> tasks = taskService.getTasksByUser(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable String status) {
        List<TaskDTO> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDTO>> getTasksByPriority(@PathVariable String priority) {
        List<TaskDTO> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks() {
        List<TaskDTO> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TaskDTO>> getTasksByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<TaskDTO> tasks = taskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable String status) {
        List<TaskDTO> tasks = taskService.getTasksByUserAndStatus(userId, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/project/{projectId}/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasksByProject(@PathVariable Long projectId) {
        List<TaskDTO> tasks = taskService.getOverdueTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/under-estimated")
    public ResponseEntity<List<TaskDTO>> getTasksUnderEstimatedHours() {
        List<TaskDTO> tasks = taskService.getTasksUnderEstimatedHours();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/over-estimated")
    public ResponseEntity<List<TaskDTO>> getTasksOverEstimatedHours() {
        List<TaskDTO> tasks = taskService.getTasksOverEstimatedHours();
        return ResponseEntity.ok(tasks);
    }

    // Estadísticas
    @GetMapping("/stats/count/status/{status}")
    public ResponseEntity<Long> getTaskCountByStatus(@PathVariable String status) {
        Long count = taskService.getTaskCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/count/project/{projectId}/status/{status}")
    public ResponseEntity<Long> getTaskCountByProjectAndStatus(
            @PathVariable Long projectId,
            @PathVariable String status) {
        Long count = taskService.getTaskCountByProjectAndStatus(projectId, status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/average-hours/project/{projectId}")
    public ResponseEntity<Double> getAverageActualHoursByProject(@PathVariable Long projectId) {
        Double average = taskService.getAverageActualHoursByProject(projectId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/stats/total-hours/project/{projectId}")
    public ResponseEntity<Double> getTotalActualHoursByProject(@PathVariable Long projectId) {
        Double total = taskService.getTotalActualHoursByProject(projectId);
        return ResponseEntity.ok(total);
    }
}
