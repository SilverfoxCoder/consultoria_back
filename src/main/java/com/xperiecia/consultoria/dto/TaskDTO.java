package com.xperiecia.consultoria.dto;

import com.xperiecia.consultoria.domain.Task;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private String projectName;
    private Long assignedToId;
    private String assignedToName;
    private String assignee;
    private String status;
    private String statusDisplay;
    private String priority;
    private String priorityDisplay;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor from Entity
    public static TaskDTO fromEntity(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setProjectId(task.getProject() != null ? task.getProject().getId() : null);
        dto.setProjectName(task.getProject() != null ? task.getProject().getName() : null);
        dto.setAssignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null);
        dto.setAssignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getName() : null);
        dto.setAssignee(task.getAssignee());
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        dto.setStatusDisplay(task.getStatus() != null ? task.getStatus().getDisplayName() : null);
        dto.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        dto.setPriorityDisplay(task.getPriority() != null ? task.getPriority().getDisplayName() : null);
        dto.setEstimatedHours(task.getEstimatedHours());
        dto.setActualHours(task.getActualHours());
        dto.setStartDate(task.getStartDate());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedDate(task.getCompletedDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }
}
