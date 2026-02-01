package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.ProjectRepository;
import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "API para pruebas y datos de ejemplo")
public class TestController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/health")
    @Operation(summary = "Verificar estado de la aplicación")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Aplicación funcionando correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seed-data")
    @Operation(summary = "Crear datos de prueba")
    public ResponseEntity<Map<String, Object>> createTestData() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Crear usuario que actuará como cliente
            User testClientUser = new User();
            testClientUser.setName("Cliente de Prueba");
            testClientUser.setEmail("juan@cliente.com");
            testClientUser.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa"); // admin123
            testClientUser.setPhone("+34 123 456 789");
            testClientUser.setRole("CLIENT"); // Rol importante
            testClientUser.setStatus("active");

            // Campos de perfil extendido
            testClientUser.setCompany("Empresa de Prueba S.L.");
            testClientUser.setIndustry("Tecnología");
            testClientUser.setAddress("Calle de Prueba 123, Madrid");
            testClientUser.setWebsite("www.cliente.com");
            testClientUser.setNotes("Cliente de prueba para desarrollo");
            // testClientUser.setLastContact(LocalDate.now()); // LocalDateTime required
            // usually, adjust if needed
            testClientUser.setTotalRevenue(new BigDecimal("50000.00"));
            testClientUser.setTotalProjects(3);

            User savedClientUser = userRepository.save(testClientUser);

            // Crear usuario admin
            User testAdminUser = new User();
            testAdminUser.setName("Usuario Admin");
            testAdminUser.setEmail("admin@test.com");
            testAdminUser.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa"); // admin123
            testAdminUser.setPhone("+34 987 654 321");
            testAdminUser.setRole("ADMIN");
            testAdminUser.setStatus("active");

            User savedAdminUser = userRepository.save(testAdminUser);

            // Crear proyecto de prueba asignado al usuario cliente
            Project testProject = new Project();
            testProject.setName("Proyecto de Prueba");
            testProject.setClient(savedClientUser); // Asignar el usuario cliente
            testProject.setStatus(Project.ProjectStatus.PLANIFICACION);
            testProject.setProgress(0);
            testProject.setStartDate(LocalDate.now());
            testProject.setEndDate(LocalDate.now().plusMonths(3));
            testProject.setBudget(new BigDecimal("10000.00"));
            testProject.setSpent(new BigDecimal("0.00"));
            testProject.setPriority(Project.ProjectPriority.MEDIA);
            testProject.setDescription("Proyecto de prueba para desarrollo");
            testProject.setJiraEnabled(false);

            Project savedProject = projectRepository.save(testProject);

            response.put("message", "Datos de prueba creados exitosamente");
            response.put("clientId", savedClientUser.getId());
            response.put("userId", savedAdminUser.getId());
            response.put("projectId", savedProject.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al crear datos de prueba: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/clients")
    @Operation(summary = "Obtener todos los clientes (Usuarios con rol CLIENT)")
    public ResponseEntity<List<User>> getAllClients() {
        return ResponseEntity.ok(userRepository.findByRole("CLIENT"));
    }

    @GetMapping("/users")
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/projects")
    @Operation(summary = "Obtener todos los proyectos")
    public ResponseEntity<Iterable<Project>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }
}
