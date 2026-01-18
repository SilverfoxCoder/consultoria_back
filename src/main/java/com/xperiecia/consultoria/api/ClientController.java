package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Client;
import com.xperiecia.consultoria.domain.ClientRepository;
import com.xperiecia.consultoria.dto.ClientDTO;
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
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "API para gestión de clientes")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los clientes")
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(ClientDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por ID")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.map(c -> ResponseEntity.ok(ClientDTO.fromEntity(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener clientes por estado")
    public List<ClientDTO> getClientsByStatus(@PathVariable String status) {
        return clientRepository.findByStatus(status).stream()
                .map(ClientDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente")
    public ClientDTO createClient(@RequestBody ClientDTO clientDTO) {
        Client client = clientDTO.toEntity();
        Client savedClient = clientRepository.save(client);
        return ClientDTO.fromEntity(savedClient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody ClientDTO clientDetails) {
        System.out.println("Updating Client ID: " + id);
        System.out.println("Payload Website: " + clientDetails.getWebsite());
        System.out.println("Payload Full: " + clientDetails);
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            Client updatedClient = client.get();
            updatedClient.setName(clientDetails.getName());
            updatedClient.setContactPerson(clientDetails.getContactPerson());
            updatedClient.setEmail(clientDetails.getEmail());
            updatedClient.setPhone(clientDetails.getPhone());
            updatedClient.setCompany(clientDetails.getCompany());
            updatedClient.setIndustry(clientDetails.getIndustry());
            updatedClient.setStatus(clientDetails.getStatus());
            updatedClient.setAddress(clientDetails.getAddress());
            updatedClient.setWebsite(clientDetails.getWebsite());
            updatedClient.setNotes(clientDetails.getNotes());
            updatedClient.setLastContact(clientDetails.getLastContact());
            updatedClient.setTotalRevenue(clientDetails.getTotalRevenue());
            updatedClient.setTotalProjects(clientDetails.getTotalProjects());

            Client savedClient = clientRepository.save(updatedClient);
            return ResponseEntity.ok(ClientDTO.fromEntity(savedClient));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            try {
                clientRepository.deleteById(id);
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
