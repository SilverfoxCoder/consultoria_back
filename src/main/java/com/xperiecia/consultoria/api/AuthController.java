package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.domain.LoginHistory;
import com.xperiecia.consultoria.domain.LoginHistoryRepository;
import com.xperiecia.consultoria.domain.Notification;
import com.xperiecia.consultoria.dto.LoginRequest;
import com.xperiecia.consultoria.dto.LoginResponse;
import com.xperiecia.consultoria.dto.ChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

/**
 * Controlador para autenticación de usuarios
 * 
 * Este controlador maneja el login y logout de usuarios,
 * incluyendo la validación de credenciales y el registro
 * del historial de login.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API para autenticación de usuarios")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private com.xperiecia.consultoria.application.NotificationService notificationService;

    @Autowired
    private com.xperiecia.consultoria.application.AdminNotificationService adminNotificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registrar un nuevo usuario
     * 
     * @param request Datos del usuario a registrar
     * @return Respuesta con el usuario registrado o error
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<LoginResponse> register(
            @RequestBody com.xperiecia.consultoria.dto.RegisterUserRequest request) {
        try {
            // Validar que el email no exista
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(false);
                response.setMessage("El email ya está registrado");
                return ResponseEntity.badRequest().body(response);
            }

            // Crear nuevo usuario
            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setEmail(request.getEmail());
            newUser.setPhone(request.getPhone());

            // Hashear contraseña
            newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

            // Asignar rol (por defecto CLIENT si no se especifica, o forzar CLIENT para
            // registros públicos)
            // Aquí permitimos que el front envíe el rol, pero podríamos restringirlo
            String role = request.getRole();
            if (role == null || role.trim().isEmpty()) {
                role = "CLIENT";
            }
            newUser.setRole(role);
            newUser.setStatus("active");
            newUser.setRegisteredAt(LocalDateTime.now());

            // Si es cliente, inicializar campos específicos si es necesario (por ahora
            // vacíos)
            if ("CLIENT".equalsIgnoreCase(role) || "CLIENTE".equalsIgnoreCase(role)) {
                // newUser.setContactPerson(newUser.getName()); // Por defecto usaremos name
            }

            User savedUser = userRepository.save(newUser);
            System.out.println("✅ Nuevo usuario registrado: " + savedUser.getEmail() + " con rol: " + role);

            Long clientId = null;
            if ("CLIENT".equalsIgnoreCase(role) || "CLIENTE".equalsIgnoreCase(role)) {
                clientId = savedUser.getId();
            }

            // Enviar notificación de bienvenida
            notificationService.createWelcomeNotification(savedUser.getId(), savedUser.getName());

            // Notificar admins
            adminNotificationService.notifyNewUserRegistration(savedUser);

            // Auto-login (retornar token)
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Registro exitoso");
            response.setToken("token_" + savedUser.getId() + "_" + System.currentTimeMillis());

            Long finalClientId = clientId; // Parametro efectivo final para lambda/map

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", savedUser.getId());
            userMap.put("name", savedUser.getName());
            userMap.put("email", savedUser.getEmail());
            userMap.put("role", savedUser.getRole());
            userMap.put("clientId", finalClientId);

            response.setUser(userMap);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error en registro: " + e.getMessage());
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Error en el servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Autenticar usuario con email y contraseña
     * 
     * @param loginRequest Datos de login (email, password)
     * @return Respuesta con token y datos del usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Buscar usuario por email
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Verificar contraseña (simplificado para desarrollo)
                // En producción usar BCrypt.checkpw()
                boolean passwordValid = false;

                // Para desarrollo, aceptar contraseñas simples o verificar hash
                if (loginRequest.getPassword().equals("password") ||
                        loginRequest.getPassword().equals("admin123") ||
                        passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                    passwordValid = true;
                }

                if (passwordValid) {

                    // Registrar login exitoso
                    LoginHistory loginHistory = new LoginHistory();
                    loginHistory.setUser(user);
                    loginHistory.setLoginAt(LocalDateTime.now());
                    loginHistoryRepository.save(loginHistory);

                    // Crear respuesta exitosa
                    LoginResponse response = new LoginResponse();
                    response.setSuccess(true);
                    response.setMessage("Login exitoso");
                    response.setToken("token_" + user.getId() + "_" + System.currentTimeMillis());

                    // Resolver clientId si aplica (ahora es el mismo ID del usuario si es cliente)
                    Long clientId = null;
                    if ("client".equalsIgnoreCase(user.getRole()) || "cliente".equalsIgnoreCase(user.getRole())) {
                        clientId = user.getId();
                    }

                    Long finalClientId = clientId;

                    response.setUser(new HashMap<>() {
                        {
                            put("id", user.getId());
                            put("name", user.getName());
                            put("email", user.getEmail());
                            put("role", user.getRole() != null ? user.getRole().toUpperCase() : null);
                            put("roles", user.getRoles() != null ? user.getRoles().stream()
                                    .map(r -> r.getName())
                                    .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
                            put("clientId", finalClientId);
                        }
                    });

                    return ResponseEntity.ok(response);
                } else {
                    // Contraseña incorrecta
                    LoginResponse response = new LoginResponse();
                    response.setSuccess(false);
                    response.setMessage("Credenciales incorrectas");
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                // Usuario no encontrado
                LoginResponse response = new LoginResponse();
                response.setSuccess(false);
                response.setMessage("Usuario no encontrado");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Error en el servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Cerrar sesión del usuario
     * 
     * @return Respuesta de confirmación
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sesión cerrada correctamente");
        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si el usuario está autenticado
     * 
     * @return Estado de autenticación
     */
    @GetMapping("/verify")
    @Operation(summary = "Verificar autenticación")
    public ResponseEntity<Map<String, Object>> verifyAuth() {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("message", "Usuario autenticado");
        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si es el primer login del usuario
     * 
     * @param userId ID del usuario
     * @return Información sobre si es el primer login
     */
    @GetMapping("/first-login/{userId}")
    @Operation(summary = "Verificar si es el primer login")
    public ResponseEntity<Map<String, Object>> checkFirstLogin(@PathVariable long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                // User user = userOptional.get();

                // Verificar si el usuario tiene historial de login
                List<LoginHistory> loginHistory = loginHistoryRepository.findByUser_Id(userId);

                Map<String, Object> response = new HashMap<>();
                response.put("userId", userId);
                response.put("isFirstLogin", loginHistory.isEmpty());
                response.put("loginCount", loginHistory.size());
                response.put("lastLogin", loginHistory.isEmpty() ? null : loginHistory.get(0).getLoginAt());

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al verificar primer login: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Login con Google OAuth
     * 
     * @param googleData Datos del usuario de Google
     * @return Respuesta con token y datos del usuario
     */
    @PostMapping("/google")
    @Operation(summary = "Login con Google OAuth")
    public ResponseEntity<LoginResponse> googleAuth(@RequestBody Map<String, Object> googleData) {
        try {
            System.out.println("🔍 Google Auth - Datos recibidos: " + googleData);

            String email = (String) googleData.get("email");
            String name = (String) googleData.get("name");
            String googleId = (String) googleData.get("googleId");

            // También buscar en "sub" si no está en "googleId"
            if (googleId == null) {
                googleId = (String) googleData.get("sub");
            }

            if (email == null || name == null) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(false);
                response.setMessage("Datos de Google incompletos");
                return ResponseEntity.badRequest().body(response);
            }

            // Buscar si el usuario ya existe
            Optional<User> existingUser = userRepository.findByEmail(email);
            final User user;

            if (existingUser.isPresent()) {
                // Usuario existente - autenticar
                user = existingUser.get();
                System.out.println("✅ Usuario existente autenticado: " + email);
            } else {
                // Crear nuevo usuario automáticamente
                User newUser = new User();
                newUser.setName(name);
                newUser.setEmail(email);

                // Usar el rol que viene de los datos de Google, por defecto 'client' para
                // registros web
                String role = (String) googleData.get("role");
                if (role == null || role.trim().isEmpty()) {
                    role = "client"; // Por defecto, usuarios web son clientes
                }
                newUser.setRole(role);

                if ("client".equalsIgnoreCase(role) || "cliente".equalsIgnoreCase(role)) {
                    // newUser.setContactPerson(name); // Removed as User uses name
                }

                newUser.setPasswordHash("google_oauth_" + googleId);
                user = userRepository.save(newUser);
                System.out.println("✅ Nuevo usuario creado y autenticado: " + email + " con rol: " + role);

                // Crear notificación de bienvenida para nuevo usuario
                notificationService.createWelcomeNotification(user.getId(), user.getName());

                // Notificar a administradores sobre nuevo registro
                adminNotificationService.notifyNewUserRegistration(user);
            }

            // Registrar login exitoso
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setUser(user);
            loginHistory.setLoginAt(LocalDateTime.now());
            loginHistoryRepository.save(loginHistory);

            // Verificar si es el primer login y crear notificación específica
            List<LoginHistory> userLoginHistory = loginHistoryRepository.findByUser_Id(user.getId());
            if (userLoginHistory.size() == 1) { // Solo este login registrado
                // Crear notificación de primer login
                Notification firstLoginNotification = new Notification(
                        "FIRST_LOGIN",
                        "¡Primer acceso exitoso!",
                        "Has completado tu primer acceso al sistema. ¡Bienvenido!",
                        "medium");
                firstLoginNotification.setTargetUserId(user.getId());
                notificationService.createNotification(firstLoginNotification);
                System.out.println("🎉 Notificación de primer login enviada a: " + user.getName());

                // Notificar a administradores sobre primer login
                adminNotificationService.notifyFirstUserLogin(user);
            }

            // Asegurar clientId si rol = client
            Long clientId = null;
            if ("client".equalsIgnoreCase(user.getRole()) || "cliente".equalsIgnoreCase(user.getRole())) {
                clientId = user.getId();
            }
            Long finalClientId = clientId;

            // Crear respuesta exitosa
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Login con Google exitoso");
            response.setToken("google_token_" + user.getId() + "_" + System.currentTimeMillis());
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole() != null ? user.getRole().toUpperCase() : null);
            userMap.put("roles", user.getRoles() != null ? user.getRoles().stream()
                    .map(r -> r.getName())
                    .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
            userMap.put("clientId", finalClientId);
            response.setUser(userMap);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error en Google Auth: " + e.getMessage());
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Error en el servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Registrar usuario con Google OAuth
     * 
     * @param googleData Datos del usuario de Google
     * @return Respuesta con token y datos del usuario
     */
    @PostMapping("/google/register")
    @Operation(summary = "Registrar usuario con Google OAuth")
    public ResponseEntity<LoginResponse> googleRegister(@RequestBody Map<String, Object> googleData) {
        try {
            System.out.println("🔍 Google Register - Datos recibidos: " + googleData);

            String email = (String) googleData.get("email");
            String name = (String) googleData.get("name");
            String googleId = (String) googleData.get("sub");

            if (email == null || name == null) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(false);
                response.setMessage("Datos de Google incompletos");
                return ResponseEntity.badRequest().body(response);
            }

            // Buscar si el usuario ya existe
            Optional<User> existingUser = userRepository.findByEmail(email);
            final User user;

            if (existingUser.isPresent()) {
                // Usuario existente - actualizar datos si es necesario
                user = existingUser.get();
                System.out.println("✅ Usuario existente encontrado: " + email);
            } else {
                // Crear nuevo usuario
                User newUser = new User();
                newUser.setName(name);
                newUser.setEmail(email);

                // Usar el rol que viene de los datos de Google, por defecto 'client' para
                // registros web
                String role = (String) googleData.get("role");
                if (role == null || role.trim().isEmpty()) {
                    role = "client"; // Por defecto, usuarios web son clientes
                }
                newUser.setRole(role);

                if ("client".equalsIgnoreCase(role) || "cliente".equalsIgnoreCase(role)) {
                    // newUser.setContactPerson(name); // Removed as User uses name
                }

                newUser.setPasswordHash("google_oauth_" + googleId);
                user = userRepository.save(newUser);
                System.out.println("✅ Nuevo usuario creado: " + email + " con rol: " + role);

                // Crear notificación de bienvenida para nuevo usuario
                notificationService.createWelcomeNotification(user.getId(), user.getName());

                // Notificar a administradores sobre nuevo registro
                adminNotificationService.notifyNewUserRegistration(user);
            }

            // Registrar login exitoso
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setUser(user);
            loginHistory.setLoginAt(LocalDateTime.now());
            loginHistoryRepository.save(loginHistory);

            // Verificar si es el primer login y crear notificación específica
            List<LoginHistory> userLoginHistory = loginHistoryRepository.findByUser_Id(user.getId());
            if (userLoginHistory.size() == 1) { // Solo este login registrado
                // Crear notificación de primer login
                Notification firstLoginNotification = new Notification(
                        "FIRST_LOGIN",
                        "¡Primer acceso exitoso!",
                        "Has completado tu primer acceso al sistema. ¡Bienvenido!",
                        "medium");
                firstLoginNotification.setTargetUserId(user.getId());
                notificationService.createNotification(firstLoginNotification);
                System.out.println("🎉 Notificación de primer login enviada a: " + user.getName());

                // Notificar a administradores sobre primer login
                adminNotificationService.notifyFirstUserLogin(user);
            }

            // Asegurar clientId si rol = client
            Long clientId = null;
            if ("client".equalsIgnoreCase(user.getRole()) || "cliente".equalsIgnoreCase(user.getRole())) {
                clientId = user.getId();
            }
            Long finalClientId = clientId;

            // Crear respuesta exitosa
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Login con Google exitoso");
            response.setToken("google_token_" + user.getId() + "_" + System.currentTimeMillis());
            response.setUser(new HashMap<>() {
                {
                    put("id", user.getId());
                    put("name", user.getName());
                    put("email", user.getEmail());
                    put("role", user.getRole() != null ? user.getRole().toUpperCase() : null);
                    put("roles", user.getRoles() != null ? user.getRoles().stream()
                            .map(r -> r.getName())
                            .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
                    put("clientId", finalClientId);
                }
            });

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error en Google Register: " + e.getMessage());
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Error en el servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Cambiar contraseña del usuario
     * 
     * @param request Datos del cambio de contraseña
     * @return Respuesta del cambio de contraseña
     */
    @PostMapping("/change-password")
    @Operation(summary = "Cambiar contraseña del usuario")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Optional<User> userOptional = userRepository.findById(request.getUserId().longValue());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Verificar contraseña actual
                boolean currentPasswordValid = false;
                if (request.getCurrentPassword().equals("password") ||
                        request.getCurrentPassword().equals("admin123") ||
                        user.getPasswordHash().equals(request.getCurrentPassword())) {
                    currentPasswordValid = true;
                }

                if (!currentPasswordValid) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Contraseña actual incorrecta");
                    return ResponseEntity.badRequest().body(response);
                }

                // Verificar que las nuevas contraseñas coincidan
                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Las nuevas contraseñas no coinciden");
                    return ResponseEntity.badRequest().body(response);
                }

                // Verificar que la nueva contraseña sea diferente
                if (request.getNewPassword().equals(request.getCurrentPassword())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "La nueva contraseña debe ser diferente a la actual");
                    return ResponseEntity.badRequest().body(response);
                }

                // Actualizar contraseña (en producción usar BCrypt)
                user.setPasswordHash(request.getNewPassword());
                userRepository.save(user);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Contraseña cambiada exitosamente");
                response.put("userId", user.getId());

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al cambiar contraseña: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Eliminar un usuario
     * 
     * @param userId ID del usuario a eliminar
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable long userId) {
        try {
            System.out.println("🗑️ Eliminando usuario con ID: " + userId);

            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();

            userRepository.delete(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario eliminado correctamente");
            response.put("userId", userId);

            System.out.println("✅ Usuario eliminado correctamente: " + user.getName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error eliminando usuario: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar el usuario");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
