package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.application.NotificationService;
import com.xperiecia.consultoria.domain.Budget;
import com.xperiecia.consultoria.domain.BudgetRepository;
import com.xperiecia.consultoria.domain.Client;
import com.xperiecia.consultoria.domain.ClientRepository;
import com.xperiecia.consultoria.dto.BudgetRequest;
import com.xperiecia.consultoria.dto.BudgetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para gestión de presupuestos
 * 
 * Este controlador maneja todas las operaciones relacionadas con
 * presupuestos, incluyendo creación, consulta, actualización y
 * gestión de estados.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "API para gestión de presupuestos")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private com.xperiecia.consultoria.application.AdminNotificationService adminNotificationService;

    /**
     * Endpoint de prueba simple para diagnosticar problemas
     */
    @PostMapping("/test-simple")
    @Operation(summary = "Endpoint de prueba simple")
    public ResponseEntity<Map<String, Object>> testSimple(@RequestBody(required = false) String rawBody) {
        try {
            System.out.println("🔍 === TEST SIMPLE ENDPOINT ===");
            System.out.println("🔍 Raw body recibido: " + rawBody);
            System.out.println("🔍 Tipo de body: " + (rawBody != null ? rawBody.getClass().getName() : "null"));
            System.out.println("🔍 Longitud del body: " + (rawBody != null ? rawBody.length() : 0));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Endpoint de prueba funcionando");
            response.put("rawBody", rawBody);
            response.put("bodyType", rawBody != null ? rawBody.getClass().getName() : "null");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en test simple: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error en endpoint de prueba");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint de prueba con Map genérico
     */
    @PostMapping("/test-map")
    @Operation(summary = "Endpoint de prueba con Map")
    public ResponseEntity<Map<String, Object>> testMap(@RequestBody(required = false) Map<String, Object> data) {
        try {
            System.out.println("🔍 === TEST MAP ENDPOINT ===");
            System.out.println("🔍 Data recibida: " + data);
            System.out.println("🔍 Tipo de data: " + (data != null ? data.getClass().getName() : "null"));

            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    System.out.println("🔍 Campo '" + entry.getKey() + "': " + entry.getValue() +
                            " (tipo: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : "null")
                            + ")");
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Endpoint Map funcionando");
            response.put("receivedData", data);
            response.put("dataType", data != null ? data.getClass().getName() : "null");
            response.put("fieldCount", data != null ? data.size() : 0);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en test map: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error en endpoint Map");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Crear un nuevo presupuesto
     * 
     * @param request Datos del presupuesto a crear
     * @return Presupuesto creado con código 201
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo presupuesto")
    public ResponseEntity<BudgetResponse> createBudget(@RequestBody BudgetRequest request) {
        try {
            System.out.println("=== DEBUG: createBudget called ===");
            System.out.println("Request: " + request);
            System.out.println("Title: " + request.getTitle());
            System.out.println("Description: " + request.getDescription());
            System.out.println("ServiceType: " + request.getServiceType());
            System.out.println("Budget: " + request.getBudget());
            System.out.println("Timeline: " + request.getTimeline());
            System.out.println("AdditionalInfo: " + request.getAdditionalInfo());
            System.out.println("ClientId: " + request.getClientId());

            // Validar que el cliente existe
            System.out.println("Buscando cliente con ID: " + request.getClientId());
            Optional<Client> clientOptional = clientRepository.findById(request.getClientId());
            if (clientOptional.isEmpty()) {
                System.out.println("ERROR: Cliente no encontrado con ID: " + request.getClientId());
                return ResponseEntity.badRequest().build();
            }

            Client client = clientOptional.get();
            System.out.println("Cliente encontrado: " + client.getName());

            // Crear el presupuesto
            Budget budget = new Budget();
            budget.setTitle(request.getTitle());
            budget.setDescription(request.getDescription());
            budget.setServiceType(request.getServiceType());
            budget.setBudget(request.getBudget());
            budget.setTimeline(request.getTimeline());
            budget.setAdditionalInfo(request.getAdditionalInfo());
            budget.setClient(client);
            budget.setStatus(Budget.BudgetStatus.PENDIENTE);

            System.out.println("Budget creado, guardando...");
            Budget savedBudget = budgetRepository.save(budget);
            System.out.println("Budget guardado con ID: " + savedBudget.getId());

            // 📢 NOTIFICACIÓN: Nuevo presupuesto creado
            try {
                notificationService.notifyNewBudget(
                        savedBudget.getId(),
                        savedBudget.getClient().getId(),
                        savedBudget.getTitle());
                System.out.println("✅ Notificación de nuevo presupuesto enviada");

                // 🔔 NOTIFICAR A ADMINISTRADORES: Nueva solicitud de presupuesto
                adminNotificationService.notifyNewBudgetRequest(
                        savedBudget.getId(),
                        savedBudget.getClient().getName(),
                        savedBudget.getTitle());
                System.out.println("🔔 Administradores notificados de nueva solicitud de presupuesto");

            } catch (Exception notifEx) {
                System.err.println("⚠️ Error enviando notificación: " + notifEx.getMessage());
                // No fallar la creación del presupuesto por error de notificación
            }

            // Convertir a DTO de respuesta
            BudgetResponse response = convertToResponse(savedBudget);
            System.out.println("Response creado: " + response.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.out.println("ERROR en createBudget: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crear un presupuesto para un cliente específico
     * 
     * @param clientId ID del cliente
     * @param request  Datos del presupuesto
     * @return Presupuesto creado
     */
    @PostMapping("/client/{clientId}")
    @Operation(summary = "Crear presupuesto para un cliente específico")
    public ResponseEntity<BudgetResponse> createBudgetForClient(
            @PathVariable Long clientId,
            @RequestBody BudgetRequest request) {

        try {
            System.out.println("=== DEBUG: createBudgetForClient called ===");
            System.out.println("ClientId from path: " + clientId);
            System.out.println("Request: " + request);
            System.out.println("Title: " + request.getTitle());
            System.out.println("Description: " + request.getDescription());
            System.out.println("ServiceType: " + request.getServiceType());
            System.out.println("Budget: " + request.getBudget());
            System.out.println("Timeline: " + request.getTimeline());
            System.out.println("AdditionalInfo: " + request.getAdditionalInfo());
            System.out.println("ClientId from request: " + request.getClientId());

            // Validar campos requeridos
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                System.err.println("❌ ERROR: Título es requerido");
                return ResponseEntity.badRequest().build();
            }

            if (request.getServiceType() == null || request.getServiceType().trim().isEmpty()) {
                System.err.println("❌ ERROR: Tipo de servicio es requerido");
                return ResponseEntity.badRequest().build();
            }

            // Validar que el cliente existe
            System.out.println("Buscando cliente con ID: " + clientId);
            Optional<Client> clientOptional = clientRepository.findById(clientId);
            if (clientOptional.isEmpty()) {
                System.err.println("❌ ERROR: Cliente no encontrado con ID: " + clientId);
                return ResponseEntity.notFound().build();
            }

            Client client = clientOptional.get();
            System.out.println("✅ Cliente encontrado: " + client.getName());

            // Crear el presupuesto
            Budget budget = new Budget();
            budget.setTitle(request.getTitle());
            budget.setDescription(request.getDescription() != null ? request.getDescription() : "");
            budget.setServiceType(request.getServiceType());
            budget.setBudget(request.getBudget());
            budget.setTimeline(request.getTimeline() != null ? request.getTimeline() : "");
            budget.setAdditionalInfo(request.getAdditionalInfo() != null ? request.getAdditionalInfo() : "");
            budget.setClient(client);
            budget.setStatus(Budget.BudgetStatus.PENDIENTE);

            System.out.println("Budget creado, guardando...");
            Budget savedBudget = budgetRepository.save(budget);
            System.out.println("✅ Budget guardado con ID: " + savedBudget.getId());

            // 📢 NOTIFICACIÓN: Nuevo presupuesto creado
            try {
                notificationService.notifyNewBudget(
                        savedBudget.getId(),
                        savedBudget.getClient().getId(),
                        savedBudget.getTitle());
                System.out.println("✅ Notificación de nuevo presupuesto enviada (createBudgetForClient)");

                // 🔔 NOTIFICAR A ADMINISTRADORES: Nueva solicitud de presupuesto
                adminNotificationService.notifyNewBudgetRequest(
                        savedBudget.getId(),
                        savedBudget.getClient().getName(),
                        savedBudget.getTitle());
                System.out.println("🔔 Administradores notificados de nueva solicitud de presupuesto");

            } catch (Exception notifEx) {
                System.err.println("⚠️ Error enviando notificación: " + notifEx.getMessage());
                System.err.println("⚠️ Stack trace: " + notifEx.getStackTrace());
                // No fallar la creación del presupuesto por error de notificación
                // El presupuesto se creó exitosamente, solo falló la notificación
            }

            // Convertir a DTO de respuesta
            BudgetResponse response = convertToResponse(savedBudget);
            System.out.println("✅ Response creado: " + response.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("❌ ERROR en createBudgetForClient: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener todos los presupuestos
     * 
     * @return Lista de todos los presupuestos
     */
    @GetMapping
    @Operation(summary = "Obtener todos los presupuestos")
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        try {
            List<Budget> budgets = budgetRepository.findAllByOrderByCreatedAtDesc();
            List<BudgetResponse> responses = budgets.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener presupuestos por cliente
     * 
     * @param clientId ID del cliente
     * @return Lista de presupuestos del cliente
     */
    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener presupuestos de un cliente")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByClient(@PathVariable Long clientId) {
        try {
            List<Budget> budgets = budgetRepository.findByClientIdOrderByCreatedAtDesc(clientId);
            List<BudgetResponse> responses = budgets.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener un presupuesto por ID
     * 
     * @param id ID del presupuesto
     * @return Presupuesto encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un presupuesto por ID")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        try {
            Optional<Budget> budgetOptional = budgetRepository.findById(id);
            if (budgetOptional.isPresent()) {
                BudgetResponse response = convertToResponse(budgetOptional.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener presupuestos por estado
     * 
     * @param status Estado del presupuesto
     * @return Lista de presupuestos con el estado especificado
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener presupuestos por estado")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByStatus(@PathVariable String status) {
        try {
            Budget.BudgetStatus budgetStatus = Budget.BudgetStatus.valueOf(status.toUpperCase());
            List<Budget> budgets = budgetRepository.findByStatusOrderByCreatedAtDesc(budgetStatus);
            List<BudgetResponse> responses = budgets.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualizar estado de un presupuesto
     * 
     * @param id            ID del presupuesto
     * @param statusRequest Datos del nuevo estado
     * @return Presupuesto actualizado
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de un presupuesto")
    public ResponseEntity<BudgetResponse> updateBudgetStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> statusRequest) {

        try {
            System.out.println("=== DEBUG: updateBudgetStatus called ===");
            System.out.println("Budget ID: " + id);
            System.out.println("Status Request: " + statusRequest);
            System.out.println("Status Request type: " + statusRequest.getClass().getSimpleName());
            System.out.println("Status Request keys: " + statusRequest.keySet());
            System.out.println("Status Request size: " + statusRequest.size());

            Optional<Budget> budgetOptional = budgetRepository.findById(id);
            if (budgetOptional.isEmpty()) {
                System.out.println("ERROR: Budget not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }

            Budget budget = budgetOptional.get();
            System.out.println("Budget found: " + budget.getTitle());

            // Manejar el caso donde status puede ser un objeto anidado
            Object statusObj = statusRequest.get("status");
            System.out.println("Status Object: " + statusObj);
            System.out.println(
                    "Status Object type: " + (statusObj != null ? statusObj.getClass().getSimpleName() : "null"));

            String newStatus;

            if (statusObj instanceof Map) {
                // Si status es un objeto, extraer el valor real
                Map<String, Object> statusMap = (Map<String, Object>) statusObj;
                System.out.println("Status Map keys: " + statusMap.keySet());
                newStatus = (String) statusMap.get("status");
                System.out.println("Status was nested object, extracted: " + newStatus);
            } else {
                // Si status es un string directo
                newStatus = (String) statusObj;
                System.out.println("Status is direct string: " + newStatus);
            }

            // Manejar responseNotes que puede estar en el objeto anidado
            String responseNotes;
            if (statusObj instanceof Map) {
                Map<String, Object> statusMap = (Map<String, Object>) statusObj;
                responseNotes = (String) statusMap.get("responseNotes");
                System.out.println("Response notes extracted from nested object: " + responseNotes);
            } else {
                responseNotes = (String) statusRequest.get("responseNotes");
            }

            System.out.println("New Status: " + newStatus);
            System.out.println("Response Notes: " + responseNotes);

            Budget.BudgetStatus budgetStatus = Budget.BudgetStatus.valueOf(newStatus.toUpperCase());
            budget.setStatus(budgetStatus);

            if (responseNotes != null) {
                budget.setResponseNotes(responseNotes);
            }

            // Si se aprueba, agregar datos de aprobación
            if (budgetStatus == Budget.BudgetStatus.APROBADO) {
                System.out.println("Processing APPROVED status...");

                // Obtener approvedBudget del objeto anidado si es necesario
                Object approvedBudgetObj;
                if (statusObj instanceof Map) {
                    Map<String, Object> statusMap = (Map<String, Object>) statusObj;
                    approvedBudgetObj = statusMap.get("approvedBudget");
                    System.out.println("Approved Budget from nested object: " + approvedBudgetObj);
                } else {
                    approvedBudgetObj = statusRequest.get("approvedBudget");
                    System.out.println("Approved Budget Object: " + approvedBudgetObj);
                }

                Double approvedBudget = null;
                if (approvedBudgetObj != null && !approvedBudgetObj.toString().trim().isEmpty()) {
                    try {
                        approvedBudget = Double.valueOf(approvedBudgetObj.toString());
                        System.out.println("Converted approved budget: " + approvedBudget);
                    } catch (NumberFormatException e) {
                        System.out.println("NumberFormatException: " + e.getMessage());
                        // Si no se puede convertir, usar el presupuesto original
                        approvedBudget = budget.getBudget();
                        System.out.println("Using original budget: " + approvedBudget);
                    }
                } else {
                    System.out.println("No approved budget provided, using original");
                    // Si no se proporciona, usar el presupuesto original
                    approvedBudget = budget.getBudget();
                }

                // Obtener approvedTimeline del objeto anidado si es necesario
                String approvedTimeline;
                if (statusObj instanceof Map) {
                    Map<String, Object> statusMap = (Map<String, Object>) statusObj;
                    approvedTimeline = (String) statusMap.get("approvedTimeline");
                    System.out.println("Approved Timeline from nested object: " + approvedTimeline);
                } else {
                    approvedTimeline = (String) statusRequest.get("approvedTimeline");
                    System.out.println("Approved Timeline: " + approvedTimeline);
                }

                if (approvedTimeline == null || approvedTimeline.trim().isEmpty()) {
                    approvedTimeline = budget.getTimeline();
                    System.out.println("Using original timeline: " + approvedTimeline);
                }

                System.out.println("Calling budget.approve with: " + approvedBudget + ", " + approvedTimeline + ", "
                        + responseNotes);
                budget.approve(approvedBudget, approvedTimeline, responseNotes);
                System.out.println("Budget approved successfully");

            } else if (budgetStatus == Budget.BudgetStatus.RECHAZADO) {
                System.out.println("Processing REJECTED status...");
                budget.reject(responseNotes);
                System.out.println("Budget rejected successfully");
            }

            Budget savedBudget = budgetRepository.save(budget);

            // 📢 NOTIFICACIÓN: Estado de presupuesto actualizado
            try {
                notificationService.notifyBudgetUpdate(
                        savedBudget.getId(),
                        savedBudget.getClient().getId(),
                        newStatus,
                        savedBudget.getTitle());
                System.out.println("✅ Notificación de actualización de estado enviada");
            } catch (Exception notifEx) {
                System.err.println("⚠️ Error enviando notificación de actualización: " + notifEx.getMessage());
                // No fallar la actualización por error de notificación
            }

            BudgetResponse response = convertToResponse(savedBudget);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== ERROR DETECTED ===");
            System.err.println("Error updating budget status for ID " + id + ": " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getSimpleName());
            System.err.println("Full stack trace:");
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Eliminar un presupuesto
     * 
     * @param id ID del presupuesto
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un presupuesto")
    public ResponseEntity<Map<String, Object>> deleteBudget(@PathVariable Long id) {
        try {
            Optional<Budget> budgetOptional = budgetRepository.findById(id);
            if (budgetOptional.isPresent()) {
                budgetRepository.deleteById(id);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Presupuesto eliminado correctamente");
                response.put("id", id);
                response.put("success", true);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Presupuesto no encontrado");
                response.put("success", false);
                response.put("id", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al eliminar el presupuesto: " + e.getMessage());
            response.put("error", e.getMessage());
            response.put("success", false);
            response.put("id", id);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint de debug para ver qué datos envía el frontend
     */
    @PostMapping("/debug")
    @Operation(summary = "Debug endpoint para ver datos del frontend")
    public ResponseEntity<Map<String, Object>> debugBudgetRequest(@RequestBody Map<String, Object> data) {
        try {
            System.out.println("🔍 DEBUG - Datos recibidos del frontend:");
            System.out.println("🔍 Tipo de datos: " + data.getClass().getName());
            System.out.println("🔍 Contenido: " + data.toString());

            // Mostrar cada campo individualmente
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                System.out.println("🔍 Campo '" + entry.getKey() + "': " + entry.getValue() + " (tipo: " +
                        (entry.getValue() != null ? entry.getValue().getClass().getName() : "null") + ")");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Datos recibidos correctamente");
            response.put("receivedData", data);
            response.put("dataType", data.getClass().getName());
            response.put("fieldCount", data.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en debug: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error procesando datos");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint de test para verificar que el controlador funciona
     */
    @GetMapping("/test")
    @Operation(summary = "Test del controlador de presupuestos")
    public ResponseEntity<Map<String, Object>> testController() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("message", "Controlador de presupuestos funcionando correctamente");
            response.put("timestamp", System.currentTimeMillis());
            response.put("endpoints", List.of(
                    "POST /api/budgets - Crear presupuesto",
                    "POST /api/budgets/client/{clientId} - Crear presupuesto para cliente",
                    "GET /api/budgets - Obtener todos los presupuestos",
                    "GET /api/budgets/{id} - Obtener presupuesto por ID",
                    "PUT /api/budgets/{id} - Actualizar presupuesto",
                    "DELETE /api/budgets/{id} - Eliminar presupuesto",
                    "POST /api/budgets/debug - Debug endpoint"));

            System.out.println("✅ Test del controlador de presupuestos exitoso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en test del controlador: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de prueba para actualizar estado
     * 
     * @return Mensaje de prueba
     */
    @PutMapping("/test-status")
    @Operation(summary = "Endpoint de prueba para actualizar estado")
    public ResponseEntity<Map<String, Object>> testStatusUpdate(@RequestBody Map<String, Object> testData) {
        try {
            System.out.println("=== TEST STATUS UPDATE ===");
            System.out.println("Test Data: " + testData);
            System.out.println("Test Data type: " + testData.getClass().getSimpleName());
            System.out.println("Test Data keys: " + testData.keySet());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test status update successful");
            response.put("receivedData", testData);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Test status update error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Endpoint de prueba para crear presupuesto
     * 
     * @return Mensaje de prueba
     */
    @PostMapping("/test-create")
    @Operation(summary = "Endpoint de prueba para crear presupuesto")
    public ResponseEntity<Map<String, Object>> testCreateBudget(@RequestBody Map<String, Object> testData) {
        try {
            System.out.println("=== TEST CREATE BUDGET ===");
            System.out.println("Test Data: " + testData);
            System.out.println("Test Data type: " + testData.getClass().getSimpleName());
            System.out.println("Test Data keys: " + testData.keySet());

            // Verificar si el cliente existe
            Object clientIdObj = testData.get("clientId");
            System.out.println("Client ID Object: " + clientIdObj);
            System.out.println("Client ID Object type: "
                    + (clientIdObj != null ? clientIdObj.getClass().getSimpleName() : "null"));

            if (clientIdObj != null) {
                Long clientId;
                if (clientIdObj instanceof Integer) {
                    clientId = ((Integer) clientIdObj).longValue();
                } else if (clientIdObj instanceof Long) {
                    clientId = (Long) clientIdObj;
                } else {
                    clientId = Long.valueOf(clientIdObj.toString());
                }

                System.out.println("Converted Client ID: " + clientId);

                Optional<Client> clientOptional = clientRepository.findById(clientId);
                if (clientOptional.isPresent()) {
                    System.out.println("Client found: " + clientOptional.get().getName());
                } else {
                    System.out.println("Client NOT found with ID: " + clientId);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test create budget successful");
            response.put("receivedData", testData);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Test create budget error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Endpoint para listar clientes disponibles
     * 
     * @return Lista de clientes
     */
    @GetMapping("/clients")
    @Operation(summary = "Listar clientes disponibles")
    public ResponseEntity<List<Map<String, Object>>> getAvailableClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            List<Map<String, Object>> clientList = clients.stream()
                    .map(client -> {
                        Map<String, Object> clientMap = new HashMap<>();
                        clientMap.put("id", client.getId());
                        clientMap.put("name", client.getName());
                        clientMap.put("email", client.getEmail());
                        return clientMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(clientList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener estadísticas de presupuestos
     * 
     * @return Estadísticas de presupuestos
     */
    @GetMapping("/statistics")
    @Operation(summary = "Obtener estadísticas de presupuestos")
    public ResponseEntity<Map<String, Object>> getBudgetStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            statistics.put("total", budgetRepository.getTotalBudgets());
            statistics.put("pending", budgetRepository.getPendingBudgets());
            statistics.put("inReview", budgetRepository.getInReviewBudgets());
            statistics.put("approved", budgetRepository.getApprovedBudgets());
            statistics.put("rejected", budgetRepository.getRejectedBudgets());

            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Convertir entidad Budget a DTO BudgetResponse
     * 
     * @param budget Entidad Budget
     * @return DTO BudgetResponse
     */
    private BudgetResponse convertToResponse(Budget budget) {
        System.out.println("=== DEBUG: convertToResponse called ===");
        System.out.println("Budget ID: " + budget.getId());
        System.out.println("Budget Status: " + budget.getStatus());

        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setTitle(budget.getTitle());
        response.setDescription(budget.getDescription());
        response.setServiceType(budget.getServiceType());
        response.setBudget(budget.getBudget());
        response.setTimeline(budget.getTimeline());
        response.setAdditionalInfo(budget.getAdditionalInfo());
        response.setClientId(budget.getClient().getId());
        response.setClientName(budget.getClient().getName());
        response.setStatus(budget.getStatus().name());
        response.setStatusDisplay(budget.getStatus().getDisplayName());
        response.setCreatedAt(budget.getCreatedAt());
        response.setUpdatedAt(budget.getUpdatedAt());
        response.setResponseDate(budget.getResponseDate());
        response.setResponseNotes(budget.getResponseNotes());
        response.setApprovedBudget(budget.getApprovedBudget());
        response.setApprovedTimeline(budget.getApprovedTimeline());

        System.out.println("Response created successfully");
        return response;
    }
}
