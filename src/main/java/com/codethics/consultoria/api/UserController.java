package com.codethics.consultoria.api;

import com.codethics.consultoria.domain.User;
import com.codethics.consultoria.domain.UserRepository;
import com.codethics.consultoria.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API para gestión de usuarios")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(u -> ResponseEntity.ok(UserDTO.fromEntity(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener un usuario por email")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(u -> ResponseEntity.ok(UserDTO.fromEntity(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        User user = userDTO.toEntity();
        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User updatedUser = user.get();
            updatedUser.setName(userDetails.getName());
            updatedUser.setEmail(userDetails.getEmail());
            updatedUser.setPasswordHash(userDetails.getPasswordHash());
            updatedUser.setRole(userDetails.getRole());
            updatedUser.setPhone(userDetails.getPhone());
            updatedUser.setStatus(userDetails.getStatus());

            User savedUser = userRepository.save(updatedUser);
            return ResponseEntity.ok(UserDTO.fromEntity(savedUser));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de un usuario")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            User user = userOptional.get();
            String newStatus = request.get("status");
            
            if (newStatus == null || newStatus.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Estado requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            user.setStatus(newStatus);
            userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado del usuario actualizado a: " + newStatus);
            response.put("id", id);
            response.put("newStatus", newStatus);
            
            System.out.println("✅ Estado del usuario " + user.getName() + " cambiado a: " + newStatus);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error cambiando estado del usuario: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al cambiar estado: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario no encontrado");
            response.put("success", false);
            response.put("id", id);
            return ResponseEntity.notFound().build();
        }
        
        User user = userOptional.get();
        
        try {
            userRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario eliminado físicamente");
            response.put("deletionType", "physical");
            response.put("id", id);
            response.put("success", true);
            
            System.out.println("🗑️ Usuario eliminado físicamente: " + user.getName());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String errorMessage = e.getMessage().toLowerCase();
            boolean isForeignKeyConstraint = errorMessage.contains("foreign key constraint") || 
                                           errorMessage.contains("constraint fails") ||
                                           errorMessage.contains("cannot delete") ||
                                           errorMessage.contains("referential integrity");
            
            if (isForeignKeyConstraint) {
                // Aplicar eliminación lógica automáticamente
                try {
                    user.setStatus("deleted");
                    userRepository.save(user);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Usuario eliminado lógicamente debido a restricciones de integridad");
                    response.put("deletionType", "logical");
                    response.put("reason", "foreign_key_constraint");
                    response.put("id", id);
                    response.put("success", true);
                    response.put("newStatus", "deleted");
                    
                    System.out.println("🔄 Usuario eliminado lógicamente por foreign key: " + user.getName());
                    return ResponseEntity.ok(response);
                    
                } catch (Exception logicalDeleteError) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Error en eliminación lógica: " + logicalDeleteError.getMessage());
                    response.put("error", logicalDeleteError.getMessage());
                    response.put("success", false);
                    response.put("id", id);
                    return ResponseEntity.internalServerError().body(response);
                }
            } else {
                // Otro tipo de error
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Error al eliminar el usuario: " + e.getMessage());
                response.put("error", e.getMessage());
                response.put("success", false);
                response.put("id", id);
                
                System.err.println("❌ Error eliminando usuario " + user.getName() + ": " + e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restaurar un usuario eliminado lógicamente")
    public ResponseEntity<Map<String, Object>> restoreUser(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            User user = userOptional.get();
            
            if (!"deleted".equals(user.getStatus())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El usuario no está eliminado lógicamente");
                response.put("currentStatus", user.getStatus());
                return ResponseEntity.badRequest().body(response);
            }
            
            user.setStatus("active");
            userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario restaurado correctamente");
            response.put("id", id);
            response.put("previousStatus", "deleted");
            response.put("newStatus", "active");
            
            System.out.println("🔄 Usuario restaurado: " + user.getName());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error restaurando usuario: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al restaurar usuario: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Obtener usuarios por estado")
    public ResponseEntity<List<UserDTO>> getUsersByStatus(@PathVariable String status) {
        try {
            List<User> users = userRepository.findAll().stream()
                .filter(user -> status.equals(user.getStatus()))
                .collect(Collectors.toList());
                
            List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
                
            System.out.println("📋 Encontrados " + users.size() + " usuarios con estado: " + status);
            return ResponseEntity.ok(userDTOs);
            
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo usuarios por estado: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}