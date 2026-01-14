package com.xperiecia.consultoria.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProjectCommentRequest {
    @NotBlank(message = "El contenido del comentario es obligatorio")
    private String text; // Using 'text' as requested in payload: Payload: { text }
}
