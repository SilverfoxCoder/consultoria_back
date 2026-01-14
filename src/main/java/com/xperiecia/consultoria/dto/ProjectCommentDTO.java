package com.xperiecia.consultoria.dto;

import com.xperiecia.consultoria.domain.ProjectComment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectCommentDTO {
    private Long id;
    private Long projectId;
    private Long userId;
    private String userName; // To display user name in frontend
    private String content;
    private LocalDateTime createdAt;

    public static ProjectCommentDTO fromEntity(ProjectComment comment) {
        ProjectCommentDTO dto = new ProjectCommentDTO();
        dto.setId(comment.getId());
        dto.setProjectId(comment.getProject().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getName());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
