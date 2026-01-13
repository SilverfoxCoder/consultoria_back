package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Role;
import com.xperiecia.consultoria.domain.RoleRepository;
import com.xperiecia.consultoria.domain.Permission;
import com.xperiecia.consultoria.domain.PermissionRepository;
import com.xperiecia.consultoria.dto.RoleDTO;
import com.xperiecia.consultoria.dto.CreateRoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    // CRUD básico
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
        return RoleDTO.fromEntity(role);
    }

    public RoleDTO createRole(CreateRoleRequest request) {
        validateRoleRequest(request);
        
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setIsActive(request.getIsActive());

        // Asignar permisos si se proporcionan
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions.stream().collect(Collectors.toSet()));
        }

        Role savedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(savedRole);
    }

    public RoleDTO updateRole(Long id, CreateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        validateRoleRequest(request);

        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setIsActive(request.getIsActive());

        // Actualizar permisos
        if (request.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions.stream().collect(Collectors.toSet()));
        }

        Role updatedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(updatedRole);
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    // Consultas especializadas
    public List<RoleDTO> getActiveRoles() {
        return roleRepository.findAllActiveRoles().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RoleDTO> getRolesByPermission(Long permissionId) {
        return roleRepository.findRolesByPermissionId(permissionId).stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RoleDTO> getRolesByUser(Long userId) {
        return roleRepository.findRolesByUserId(userId).stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RoleDTO> searchRolesByName(String name) {
        return roleRepository.findActiveRolesByNameContaining(name).stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con nombre: " + name));
        return RoleDTO.fromEntity(role);
    }

    // Gestión de permisos
    public RoleDTO addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + permissionId));

        role.addPermission(permission);
        Role savedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(savedRole);
    }

    public RoleDTO removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + permissionId));

        role.removePermission(permission);
        Role savedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(savedRole);
    }

    // Estadísticas
    public Long getActiveRoleCount() {
        return roleRepository.countActiveRoles();
    }

    // Validaciones
    private void validateRoleRequest(CreateRoleRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("El nombre del rol es obligatorio");
        }

        if (request.getName().length() > 100) {
            throw new RuntimeException("El nombre del rol no puede exceder 100 caracteres");
        }

        // Verificar si el nombre ya existe (excepto para actualizaciones)
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + request.getName());
        }
    }
} 
