package com.xperiecia.consultoria.dto;

import com.xperiecia.consultoria.domain.TimeEntry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long projectId;
    private String projectName;
    private Long taskId;
    private String taskTitle;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal durationHours;
    private String description;
    private String status;
    private String statusDisplay;
    private Boolean billable;
    private BigDecimal billingRate;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor from Entity
    public static TimeEntryDTO fromEntity(TimeEntry timeEntry) {
        TimeEntryDTO dto = new TimeEntryDTO();
        dto.setId(timeEntry.getId());
        dto.setUserId(timeEntry.getUser() != null ? timeEntry.getUser().getId() : null);
        dto.setUserName(timeEntry.getUser() != null ? timeEntry.getUser().getName() : null);
        dto.setProjectId(timeEntry.getProject() != null ? timeEntry.getProject().getId() : null);
        dto.setProjectName(timeEntry.getProject() != null ? timeEntry.getProject().getName() : null);
        dto.setTaskId(timeEntry.getTask() != null ? timeEntry.getTask().getId() : null);
        dto.setTaskTitle(timeEntry.getTask() != null ? timeEntry.getTask().getTitle() : null);
        dto.setDate(timeEntry.getDate());
        dto.setStartTime(timeEntry.getStartTime());
        dto.setEndTime(timeEntry.getEndTime());
        dto.setDurationHours(timeEntry.getDurationHours());
        dto.setDescription(timeEntry.getDescription());
        dto.setStatus(timeEntry.getStatus() != null ? timeEntry.getStatus().name() : null);
        dto.setStatusDisplay(timeEntry.getStatus() != null ? timeEntry.getStatus().getDisplayName() : null);
        dto.setBillable(timeEntry.getBillable());
        dto.setBillingRate(timeEntry.getBillingRate());
        dto.setTotalAmount(timeEntry.getTotalAmount());
        dto.setCreatedAt(timeEntry.getCreatedAt());
        dto.setUpdatedAt(timeEntry.getUpdatedAt());
        return dto;
    }
} 
