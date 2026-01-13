package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.ProjectTask;
import com.xperiecia.consultoria.domain.ProjectTaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project-tasks")
@Tag(name = "Project Tasks", description = "API para gestión de tareas de proyecto")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @GetMapping
    @Operation(summary = "Obtener todas las tareas de proyecto")
    public List<ProjectTask> getAllProjectTasks() {
        return projectTaskRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una tarea por ID")
    public ResponseEntity<ProjectTask> getProjectTaskById(@PathVariable Long id) {
        Optional<ProjectTask> task = projectTaskRepository.findById(id);
        return task.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Obtener tareas por proyecto")
    public List<ProjectTask> getProjectTasksByProject(@PathVariable Long projectId) {
        return projectTaskRepository.findByProjectId(projectId);
    }

    @GetMapping("/project/{projectId}/status/{status}")
    @Operation(summary = "Obtener tareas por proyecto y estado")
    public List<ProjectTask> getProjectTasksByProjectAndStatus(@PathVariable Long projectId,
            @PathVariable String status) {
        return projectTaskRepository.findByProjectIdAndStatus(projectId, status);
    }

    @GetMapping("/assignee/{assignee}")
    @Operation(summary = "Obtener tareas por asignado")
    public List<ProjectTask> getProjectTasksByAssignee(@PathVariable String assignee) {
        return projectTaskRepository.findByAssignee(assignee);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva tarea")
    public ProjectTask createProjectTask(@RequestBody ProjectTask projectTask) {
        return projectTaskRepository.save(projectTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una tarea")
    public ResponseEntity<ProjectTask> updateProjectTask(@PathVariable Long id, @RequestBody ProjectTask taskDetails) {
        Optional<ProjectTask> task = projectTaskRepository.findById(id);
        if (task.isPresent()) {
            ProjectTask updatedTask = task.get();
            updatedTask.setProject(taskDetails.getProject());
            updatedTask.setTitle(taskDetails.getTitle());
            updatedTask.setStatus(taskDetails.getStatus());
            updatedTask.setAssignee(taskDetails.getAssignee());

            return ResponseEntity.ok(projectTaskRepository.save(updatedTask));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    public ResponseEntity<Void> deleteProjectTask(@PathVariable Long id) {
        Optional<ProjectTask> task = projectTaskRepository.findById(id);
        if (task.isPresent()) {
            projectTaskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
