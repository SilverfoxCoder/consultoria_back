package com.codethics.consultoria.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    private String description;

    private Boolean isActive = true;

    private List<Long> permissionIds;
}