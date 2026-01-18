package com.xperiecia.consultoria.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Long projectId;
    private Long assignedToId;
    private String assignee;
    private String status;
    private String priority;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completedDate;
}
