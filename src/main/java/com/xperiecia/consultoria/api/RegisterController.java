package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.dto.RegisterUserRequest;
import com.xperiecia.consultoria.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para registro de usuarios
 * 
 * Este controlador maneja el registro de nuevos usuarios
 * desde el área de cliente y desde las llamadas a la acción del home.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/register")
@Tag(name = "User Registration", description = "API para registro de usuarios")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Registrar un nuevo usuario
     * 
     * @param request Datos del usuario a registrar
     * @return Usuario registrado o error
     */
    @PostMapping("/user")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterUserRequest request) {
        try {
            // Validaciones básicas
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El nombre es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El email es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "La contraseña es obligatoria");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar que las contraseñas coincidan
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Las contraseñas no coinciden");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar que el email no esté en uso
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El email ya está registrado");
                return ResponseEntity.badRequest().body(response);
            }

            // Crear nuevo usuario
            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setEmail(request.getEmail());
            newUser.setPasswordHash(request.getPassword()); // En producción usar BCrypt
            newUser.setPhone(request.getPhone());
            newUser.setRole(request.getRole() != null ? request.getRole() : "user");
            newUser.setRegisteredAt(LocalDateTime.now());
            newUser.setStatus("active");

            User savedUser = userRepository.save(newUser);

            // Crear respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("user", new HashMap<String, Object>() {{
                put("id", savedUser.getId());
                put("name", savedUser.getName());
                put("email", savedUser.getEmail());
                put("role", savedUser.getRole());
                put("phone", savedUser.getPhone());
            }});

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Verificar si un email está disponible
     * 
     * @param email Email a verificar
     * @return Información sobre disponibilidad del email
     */
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Verificar disponibilidad de email")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@PathVariable String email) {
        try {
            boolean isAvailable = !userRepository.findByEmail(email).isPresent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Email disponible" : "Email ya registrado");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al verificar email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 
