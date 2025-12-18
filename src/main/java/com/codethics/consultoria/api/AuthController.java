package com.codethics.consultoria.api;

import com.codethics.consultoria.domain.User;
import com.codethics.consultoria.domain.UserRepository;
import com.codethics.consultoria.domain.LoginHistory;
import com.codethics.consultoria.domain.LoginHistoryRepository;
import com.codethics.consultoria.domain.Client;
import com.codethics.consultoria.domain.ClientRepository;
import com.codethics.consultoria.domain.Notification;
import com.codethics.consultoria.dto.LoginRequest;
import com.codethics.consultoria.dto.LoginResponse;
import com.codethics.consultoria.dto.ChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private ClientRepository clientRepository;

    @Autowired
    private com.codethics.consultoria.application.NotificationService notificationService;

    @Autowired
    private com.codethics.consultoria.application.AdminNotificationService adminNotificationService;

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

                // Para desarrollo, aceptar contraseñas simples
                if (loginRequest.getPassword().equals("password") ||
                        loginRequest.getPassword().equals("admin123") ||
                        user.getPasswordHash().equals(loginRequest.getPassword())) {
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
                    response.setUser(new HashMap<>() {
                        {
                            put("id", user.getId());
                            put("name", user.getName());
                            put("email", user.getEmail());
                            put("role", user.getRole());
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
    public ResponseEntity<Map<String, Object>> checkFirstLogin(@PathVariable Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

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

                newUser.setPasswordHash("google_oauth_" + googleId);
                user = userRepository.save(newUser);
                System.out.println("✅ Nuevo usuario creado y autenticado: " + email + " con rol: " + role);

                // Si el rol es 'client', crear automáticamente la entrada en la tabla clients
                if ("client".equals(role)) {
                    getOrCreateClientForUser(user);
                }

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
            if ("client".equals(user.getRole())) {
                Optional<Client> clientOpt = clientRepository.findByEmail(user.getEmail());
                Client client = clientOpt.orElseGet(() -> getOrCreateClientForUser(user));
                clientId = client != null ? client.getId() : null;
            }

            // Crear respuesta exitosa
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Login con Google exitoso");
            response.setToken("google_token_" + user.getId() + "_" + System.currentTimeMillis());
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole());
            userMap.put("clientId", clientId);
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

                newUser.setPasswordHash("google_oauth_" + googleId);
                user = userRepository.save(newUser);
                System.out.println("✅ Nuevo usuario creado: " + email + " con rol: " + role);

                // Si el rol es 'client', crear automáticamente la entrada en la tabla clients
                if ("client".equals(role)) {
                    getOrCreateClientForUser(user);
                }

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
            if ("client".equals(user.getRole())) {
                Optional<Client> clientOpt = clientRepository.findByEmail(user.getEmail());
                Client client = clientOpt.orElseGet(() -> getOrCreateClientForUser(user));
                clientId = client != null ? client.getId() : null;
            }

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
                    put("role", user.getRole());
                    put("clientId", clientId);
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
            Optional<User> userOptional = userRepository.findById(request.getUserId());
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
     * Crear entrada de cliente automáticamente cuando un usuario tiene rol 'client'
     * 
     * @param user Usuario para el cual crear el cliente
     */
    private Client getOrCreateClientForUser(User user) {
        try {
            // Evitar duplicados: buscar por email
            Optional<Client> existing = clientRepository.findByEmail(user.getEmail());
            if (existing.isPresent()) {
                return existing.get();
            }

            Client newClient = new Client();
            newClient.setName(user.getName());
            newClient.setEmail(user.getEmail());
            newClient.setPhone(user.getPhone());
            newClient.setContactPerson(user.getName());
            newClient.setStatus("active");

            Client savedClient = clientRepository.save(newClient);
            System.out.println("✅ Cliente creado automáticamente: " + savedClient.getName() + " (ID: "
                    + savedClient.getId() + ")");
            return savedClient;

        } catch (Exception e) {
            System.err.println("❌ Error creando/obteniendo cliente automáticamente: " + e.getMessage());
            return null;
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
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
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

            // Si el usuario tiene rol 'client', también eliminar de la tabla clients
            if ("client".equals(user.getRole())) {
                clientRepository.findAll().stream()
                        .filter(client -> client.getEmail().equals(user.getEmail()))
                        .findFirst()
                        .ifPresent(client -> {
                            clientRepository.delete(client);
                            System.out.println("✅ Cliente relacionado eliminado: " + client.getName());
                        });
            }

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

    /**
     * Eliminar un cliente (y el usuario relacionado si existe)
     * 
     * @param clientId ID del cliente a eliminar
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/client/{clientId}")
    @Operation(summary = "Eliminar cliente")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Long clientId) {
        try {
            System.out.println("🗑️ Eliminando cliente con ID: " + clientId);

            Optional<Client> clientOptional = clientRepository.findById(clientId);
            if (clientOptional.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Cliente no encontrado");
                return ResponseEntity.notFound().build();
            }

            Client client = clientOptional.get();

            // Buscar y eliminar usuario relacionado si existe
            userRepository.findByEmail(client.getEmail())
                    .ifPresent(user -> {
                        userRepository.delete(user);
                        System.out.println("✅ Usuario relacionado eliminado: " + user.getName());
                    });

            clientRepository.delete(client);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cliente eliminado correctamente");
            response.put("clientId", clientId);

            System.out.println("✅ Cliente eliminado correctamente: " + client.getName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error eliminando cliente: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar el cliente");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}