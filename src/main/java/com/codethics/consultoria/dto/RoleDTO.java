package com.codethics.consultoria.dto;

import com.codethics.consultoria.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> permissionIds;
    private List<String> permissionNames;
    private Long userCount;

    // Constructor from Entity
    public static RoleDTO fromEntity(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setIsActive(role.getIsActive());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());

        // Mapear permisos
        if (role.getPermissions() != null) {
            dto.setPermissionIds(role.getPermissions().stream()
                    .map(permission -> permission.getId())
                    .collect(Collectors.toList()));
            dto.setPermissionNames(role.getPermissions().stream()
                    .map(permission -> permission.getName())
                    .collect(Collectors.toList()));
        }

        // Contar usuarios
        if (role.getUsers() != null) {
            dto.setUserCount((long) role.getUsers().size());
        }

        return dto;
    }
}