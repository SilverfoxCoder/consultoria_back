package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.dto.ClientDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "API para gestión de clientes")
public class ClientController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los clientes")
    public List<ClientDTO> getAllClients() {
        // Enforce fetching only users with ROLE 'CLIENT'
        return userRepository.findByRoleIgnoreCase("Cliente").stream()
                .map(ClientDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por ID")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);
        // Only return if it's a client? For now, let's assume if fetched via this API
        // it should be valid or checks role
        if (user.isPresent() && ("CLIENT".equalsIgnoreCase(user.get().getRole())
                || "CLIENTE".equalsIgnoreCase(user.get().getRole()))) {
            return ResponseEntity.ok(ClientDTO.fromEntity(user.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener clientes por estado")
    public List<ClientDTO> getClientsByStatus(@PathVariable String status) {
        return userRepository.findByStatus(status).stream()
                .filter(u -> "CLIENT".equalsIgnoreCase(u.getRole()) || "CLIENTE".equalsIgnoreCase(u.getRole()))
                .map(ClientDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente")
    public ClientDTO createClient(@RequestBody ClientDTO clientDTO) {
        User user = clientDTO.toEntity();
        // Ensure role is set
        user.setRole("Cliente");
        // Ensure password hash (mock for now if not provided, or handle creation logic)
        if (user.getPasswordHash() == null) {
            user.setPasswordHash("$2a$10$defaultHashForClient");
        }
        user.setRegisteredAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return ClientDTO.fromEntity(savedUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable long id, @RequestBody ClientDTO clientDetails) {
        System.out.println("Updating Client (User) ID: " + id);
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();

            // Update fields
            existingUser.setName(clientDetails.getName());
            existingUser.setEmail(clientDetails.getEmail());
            existingUser.setPhone(clientDetails.getPhone());
            existingUser.setCompany(clientDetails.getCompany());
            existingUser.setIndustry(clientDetails.getIndustry());
            existingUser.setStatus(clientDetails.getStatus());
            existingUser.setAddress(clientDetails.getAddress());
            existingUser.setWebsite(clientDetails.getWebsite());
            existingUser.setNotes(clientDetails.getNotes());
            if (clientDetails.getLastContact() != null) {
                existingUser.setLastContact(clientDetails.getLastContact().atStartOfDay());
            }
            existingUser.setTotalRevenue(clientDetails.getTotalRevenue());
            existingUser.setTotalProjects(clientDetails.getTotalProjects());

            User savedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(ClientDTO.fromEntity(savedUser));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            try {
                userRepository.deleteById(id);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Cliente eliminado correctamente");
                response.put("id", id);
                response.put("success", true);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Error al eliminar el cliente: " + e.getMessage());
                response.put("error", e.getMessage());
                response.put("success", false);
                response.put("id", id);
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cliente no encontrado");
            response.put("success", false);
            response.put("id", id);
            return ResponseEntity.notFound().build();
        }
    }
}
