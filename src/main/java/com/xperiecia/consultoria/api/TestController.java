package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Client;
import com.xperiecia.consultoria.domain.ClientRepository;
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

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "API para pruebas y datos de ejemplo")
public class TestController {

    @Autowired
    private ClientRepository clientRepository;

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
            // Crear cliente de prueba
            Client testClient = new Client();
            testClient.setName("Cliente de Prueba");
            testClient.setContactPerson("Juan Pérez");
            testClient.setEmail("juan@cliente.com");
            testClient.setPhone("+34 123 456 789");
            testClient.setCompany("Empresa de Prueba S.L.");
            testClient.setIndustry("Tecnología");
            testClient.setStatus("ACTIVO");
            testClient.setAddress("Calle de Prueba 123, Madrid");
            testClient.setWebsite("www.cliente.com");
            testClient.setNotes("Cliente de prueba para desarrollo");
            testClient.setLastContact(LocalDate.now());
            testClient.setTotalRevenue(new BigDecimal("50000.00"));
            testClient.setTotalProjects(3);

            Client savedClient = clientRepository.save(testClient);

            // Crear usuario de prueba
            User testUser = new User();
            testUser.setName("Usuario de Prueba");
            testUser.setEmail("admin@test.com");
            testUser.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa"); // admin123
            testUser.setPhone("+34 987 654 321");
            testUser.setRole("ADMIN");
            testUser.setStatus("ACTIVO");

            User savedUser = userRepository.save(testUser);

            // Crear proyecto de prueba
            Project testProject = new Project();
            testProject.setName("Proyecto de Prueba");
            testProject.setClient(savedClient);
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
            response.put("clientId", savedClient.getId());
            response.put("userId", savedUser.getId());
            response.put("projectId", savedProject.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al crear datos de prueba: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/clients")
    @Operation(summary = "Obtener todos los clientes")
    public ResponseEntity<Iterable<Client>> getAllClients() {
        return ResponseEntity.ok(clientRepository.findAll());
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
