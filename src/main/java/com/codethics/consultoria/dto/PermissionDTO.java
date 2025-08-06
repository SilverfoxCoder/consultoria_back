package com.codethics.consultoria.dto;

import com.codethics.consultoria.domain.Permission;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private String name;
    private String description;
    private String resource;
    private String action;
    private String fullPermission;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> roleIds;
    private List<String> roleNames;
    private Long roleCount;

    // Constructor from Entity
    public static PermissionDTO fromEntity(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        dto.setResource(permission.getResource());
        dto.setAction(permission.getAction());
        dto.setFullPermission(permission.getFullPermission());
        dto.setIsActive(permission.getIsActive());
        dto.setCreatedAt(permission.getCreatedAt());
        dto.setUpdatedAt(permission.getUpdatedAt());

        // Mapear roles
        if (permission.getRoles() != null) {
            dto.setRoleIds(permission.getRoles().stream()
                    .map(role -> role.getId())
                    .collect(Collectors.toList()));
            dto.setRoleNames(permission.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList()));
        }

        // Contar roles
        if (permission.getRoles() != null) {
            dto.setRoleCount((long) permission.getRoles().size());
        }

        return dto;
    }
}