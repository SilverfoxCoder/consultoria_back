package com.codethics.consultoria.api;

import com.codethics.consultoria.domain.Service;
import com.codethics.consultoria.domain.ServiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Services", description = "API para gestión de servicios")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los servicios")
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un servicio por ID")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        Optional<Service> service = serviceRepository.findById(id);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener servicios por cliente")
    public List<Service> getServicesByClient(@PathVariable Long clientId) {
        return serviceRepository.findByClientId(clientId);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener servicios por estado")
    public List<Service> getServicesByStatus(@PathVariable String status) {
        return serviceRepository.findByStatus(status);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtener servicios por tipo")
    public List<Service> getServicesByType(@PathVariable String type) {
        return serviceRepository.findByType(type);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo servicio")
    public Service createService(@RequestBody Service service) {
        return serviceRepository.save(service);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un servicio")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service serviceDetails) {
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            Service updatedService = service.get();
            updatedService.setClient(serviceDetails.getClient());
            updatedService.setTitle(serviceDetails.getTitle());
            updatedService.setDescription(serviceDetails.getDescription());
            updatedService.setType(serviceDetails.getType());
            updatedService.setStatus(serviceDetails.getStatus());
            updatedService.setStartDate(serviceDetails.getStartDate());
            updatedService.setEndDate(serviceDetails.getEndDate());
            updatedService.setAmount(serviceDetails.getAmount());
            updatedService.setInvoice(serviceDetails.getInvoice());
            updatedService.setInvoiceStatus(serviceDetails.getInvoiceStatus());

            return ResponseEntity.ok(serviceRepository.save(updatedService));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un servicio")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            serviceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}