package com.xperiecia.consultoria.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectReportDTO {
    private Float efficiency;
    private Float hoursLogged;
    private Long completedTasks;
    private Long totalTasks;
    private String summary;
}
