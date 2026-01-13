package com.xperiecia.consultoria.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionRequest {

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    private String description;

    @NotBlank(message = "El recurso es obligatorio")
    @Size(max = 100, message = "El recurso no puede exceder 100 caracteres")
    private String resource;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 50, message = "La acción no puede exceder 50 caracteres")
    private String action;

    private Boolean isActive = true;

    private List<Long> roleIds;
}
