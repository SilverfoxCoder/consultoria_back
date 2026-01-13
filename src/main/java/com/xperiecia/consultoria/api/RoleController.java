package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.application.RoleService;
import com.xperiecia.consultoria.dto.RoleDTO;
import com.xperiecia.consultoria.dto.CreateRoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // CRUD básico
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        RoleDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleDTO createdRole = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @Valid @RequestBody CreateRoleRequest request) {
        RoleDTO updatedRole = roleService.updateRole(id, request);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // Consultas especializadas
    @GetMapping("/active")
    public ResponseEntity<List<RoleDTO>> getActiveRoles() {
        List<RoleDTO> roles = roleService.getActiveRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/permission/{permissionId}")
    public ResponseEntity<List<RoleDTO>> getRolesByPermission(@PathVariable Long permissionId) {
        List<RoleDTO> roles = roleService.getRolesByPermission(permissionId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoleDTO>> getRolesByUser(@PathVariable Long userId) {
        List<RoleDTO> roles = roleService.getRolesByUser(userId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RoleDTO>> searchRolesByName(@RequestParam String name) {
        List<RoleDTO> roles = roleService.searchRolesByName(name);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {
        RoleDTO role = roleService.getRoleByName(name);
        return ResponseEntity.ok(role);
    }

    // Gestión de permisos
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RoleDTO> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        RoleDTO role = roleService.addPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RoleDTO> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        RoleDTO role = roleService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(role);
    }

    // Estadísticas
    @GetMapping("/stats/count/active")
    public ResponseEntity<Long> getActiveRoleCount() {
        Long count = roleService.getActiveRoleCount();
        return ResponseEntity.ok(count);
    }
} 
