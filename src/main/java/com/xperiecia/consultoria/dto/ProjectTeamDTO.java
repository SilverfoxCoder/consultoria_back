package com.xperiecia.consultoria.dto;

import com.xperiecia.consultoria.domain.ProjectTeam;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String role;

    // Constructor from Entity
    public static ProjectTeamDTO fromEntity(ProjectTeam projectTeam) {
        ProjectTeamDTO dto = new ProjectTeamDTO();
        dto.setId(projectTeam.getId());
        dto.setProjectId(projectTeam.getProject() != null ? projectTeam.getProject().getId() : null);
        dto.setProjectName(projectTeam.getProject() != null ? projectTeam.getProject().getName() : null);
        dto.setName(projectTeam.getName());
        dto.setRole(projectTeam.getRole());
        return dto;
    }

    // Método para convertir DTO a Entity
    public ProjectTeam toEntity() {
        ProjectTeam projectTeam = new ProjectTeam();
        projectTeam.setId(this.id);
        projectTeam.setName(this.name);
        projectTeam.setRole(this.role);
        // El project se manejaría por separado si es necesario
        return projectTeam;
    }
}
