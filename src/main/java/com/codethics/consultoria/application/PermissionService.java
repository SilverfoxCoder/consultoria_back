package com.codethics.consultoria.application;

import com.codethics.consultoria.domain.Permission;
import com.codethics.consultoria.domain.PermissionRepository;
import com.codethics.consultoria.domain.Role;
import com.codethics.consultoria.domain.RoleRepository;
import com.codethics.consultoria.dto.PermissionDTO;
import com.codethics.consultoria.dto.CreatePermissionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    // CRUD básico
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + id));
        return PermissionDTO.fromEntity(permission);
    }

    public PermissionDTO createPermission(CreatePermissionRequest request) {
        validatePermissionRequest(request);

        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setResource(request.getResource());
        permission.setAction(request.getAction());
        permission.setIsActive(request.getIsActive());

        // Asignar roles si se proporcionan
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            permission.setRoles(roles.stream().collect(Collectors.toSet()));
        }

        Permission savedPermission = permissionRepository.save(permission);
        return PermissionDTO.fromEntity(savedPermission);
    }

    public PermissionDTO updatePermission(Long id, CreatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + id));

        validatePermissionRequest(request);

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setResource(request.getResource());
        permission.setAction(request.getAction());
        permission.setIsActive(request.getIsActive());

        // Actualizar roles
        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            permission.setRoles(roles.stream().collect(Collectors.toSet()));
        }

        Permission updatedPermission = permissionRepository.save(permission);
        return PermissionDTO.fromEntity(updatedPermission);
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permiso no encontrado con ID: " + id);
        }
        permissionRepository.deleteById(id);
    }

    // Consultas especializadas
    public List<PermissionDTO> getActivePermissions() {
        return permissionRepository.findAllActivePermissions().stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PermissionDTO> getPermissionsByRole(Long roleId) {
        return permissionRepository.findPermissionsByRoleId(roleId).stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PermissionDTO> getPermissionsByResource(String resource) {
        return permissionRepository.findActivePermissionsByResource(resource).stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PermissionDTO> getPermissionsByAction(String action) {
        return permissionRepository.findActivePermissionsByAction(action).stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PermissionDTO> searchPermissionsByName(String name) {
        return permissionRepository.findActivePermissionsByNameContaining(name).stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PermissionDTO getPermissionByName(String name) {
        Permission permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con nombre: " + name));
        return PermissionDTO.fromEntity(permission);
    }

    public PermissionDTO getPermissionByResourceAndAction(String resource, String action) {
        List<Permission> permissions = permissionRepository.findByResourceAndAction(resource, action);
        if (permissions.isEmpty()) {
            throw new RuntimeException("Permiso no encontrado para recurso: " + resource + " y acción: " + action);
        }
        return PermissionDTO.fromEntity(permissions.get(0));
    }

    // Listas de recursos y acciones
    public List<String> getAllActiveResources() {
        return permissionRepository.findAllActiveResources();
    }

    public List<String> getAllActiveActions() {
        return permissionRepository.findAllActiveActions();
    }

    // Estadísticas
    public Long getActivePermissionCount() {
        return permissionRepository.countActivePermissions();
    }

    // Validaciones
    private void validatePermissionRequest(CreatePermissionRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("El nombre del permiso es obligatorio");
        }

        if (request.getResource() == null || request.getResource().trim().isEmpty()) {
            throw new RuntimeException("El recurso es obligatorio");
        }

        if (request.getAction() == null || request.getAction().trim().isEmpty()) {
            throw new RuntimeException("La acción es obligatoria");
        }

        if (request.getName().length() > 100) {
            throw new RuntimeException("El nombre del permiso no puede exceder 100 caracteres");
        }

        if (request.getResource().length() > 100) {
            throw new RuntimeException("El recurso no puede exceder 100 caracteres");
        }

        if (request.getAction().length() > 50) {
            throw new RuntimeException("La acción no puede exceder 50 caracteres");
        }

        // Verificar si el nombre ya existe
        if (permissionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un permiso con el nombre: " + request.getName());
        }

        // Verificar si la combinación resource:action ya existe
        if (permissionRepository.existsByResourceAndAction(request.getResource(), request.getAction())) {
            throw new RuntimeException("Ya existe un permiso para el recurso: " + request.getResource() + " y acción: "
                    + request.getAction());
        }
    }
}