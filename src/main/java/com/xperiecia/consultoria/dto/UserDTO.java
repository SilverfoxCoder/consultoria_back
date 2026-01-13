package com.xperiecia.consultoria.dto;

import com.xperiecia.consultoria.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private String phone;
    private LocalDateTime registeredAt;
    private String status;
    private List<Long> roleIds; // Solo IDs de roles para evitar recursión

    // Constructor from Entity
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPasswordHash(user.getPasswordHash());
        dto.setRole(user.getRole());
        dto.setPhone(user.getPhone());
        dto.setRegisteredAt(user.getRegisteredAt());
        dto.setStatus(user.getStatus());

        // Convertir roles a solo IDs para evitar recursión
        if (user.getRoles() != null) {
            dto.setRoleIds(user.getRoles().stream()
                    .map(role -> role.getId())
                    .collect(Collectors.toList()));
        } else {
            dto.setRoleIds(new ArrayList<>());
        }

        return dto;
    }

    // Método para convertir DTO a Entity
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPasswordHash(this.passwordHash);
        user.setRole(this.role);
        user.setPhone(this.phone);
        user.setRegisteredAt(this.registeredAt);
        user.setStatus(this.status);
        // Los roles se manejarían por separado si es necesario
        return user;
    }
}
