package com.codethics.consultoria.api;

import com.codethics.consultoria.application.PermissionService;
import com.codethics.consultoria.dto.PermissionDTO;
import com.codethics.consultoria.dto.CreatePermissionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    // CRUD básico
    @GetMapping
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
        PermissionDTO permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @PostMapping
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionDTO createdPermission = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id, @Valid @RequestBody CreatePermissionRequest request) {
        PermissionDTO updatedPermission = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(updatedPermission);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    // Consultas especializadas
    @GetMapping("/active")
    public ResponseEntity<List<PermissionDTO>> getActivePermissions() {
        List<PermissionDTO> permissions = permissionService.getActivePermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByRole(@PathVariable Long roleId) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByRole(roleId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/resource/{resource}")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByResource(@PathVariable String resource) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByResource(resource);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByAction(@PathVariable String action) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByAction(action);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PermissionDTO>> searchPermissionsByName(@RequestParam String name) {
        List<PermissionDTO> permissions = permissionService.searchPermissionsByName(name);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PermissionDTO> getPermissionByName(@PathVariable String name) {
        PermissionDTO permission = permissionService.getPermissionByName(name);
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/resource/{resource}/action/{action}")
    public ResponseEntity<PermissionDTO> getPermissionByResourceAndAction(@PathVariable String resource, @PathVariable String action) {
        PermissionDTO permission = permissionService.getPermissionByResourceAndAction(resource, action);
        return ResponseEntity.ok(permission);
    }

    // Listas de recursos y acciones
    @GetMapping("/resources")
    public ResponseEntity<List<String>> getAllActiveResources() {
        List<String> resources = permissionService.getAllActiveResources();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/actions")
    public ResponseEntity<List<String>> getAllActiveActions() {
        List<String> actions = permissionService.getAllActiveActions();
        return ResponseEntity.ok(actions);
    }

    // Estadísticas
    @GetMapping("/stats/count/active")
    public ResponseEntity<Long> getActivePermissionCount() {
        Long count = permissionService.getActivePermissionCount();
        return ResponseEntity.ok(count);
    }
} 