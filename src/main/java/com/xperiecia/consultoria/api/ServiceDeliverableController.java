package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.ServiceDeliverable;
import com.xperiecia.consultoria.domain.ServiceDeliverableRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service-deliverables")
@Tag(name = "Service Deliverables", description = "API para gestión de entregables de servicio")
public class ServiceDeliverableController {

    @Autowired
    private ServiceDeliverableRepository serviceDeliverableRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los entregables de servicio")
    public List<ServiceDeliverable> getAllServiceDeliverables() {
        return serviceDeliverableRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un entregable por ID")
    public ResponseEntity<ServiceDeliverable> getServiceDeliverableById(@PathVariable Long id) {
        Optional<ServiceDeliverable> deliverable = serviceDeliverableRepository.findById(id);
        return deliverable.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{serviceId}")
    @Operation(summary = "Obtener entregables por servicio")
    public List<ServiceDeliverable> getServiceDeliverablesByService(@PathVariable Long serviceId) {
        return serviceDeliverableRepository.findByServiceId(serviceId);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo entregable")
    public ServiceDeliverable createServiceDeliverable(@RequestBody ServiceDeliverable serviceDeliverable) {
        return serviceDeliverableRepository.save(serviceDeliverable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un entregable")
    public ResponseEntity<ServiceDeliverable> updateServiceDeliverable(@PathVariable Long id,
            @RequestBody ServiceDeliverable deliverableDetails) {
        Optional<ServiceDeliverable> deliverable = serviceDeliverableRepository.findById(id);
        if (deliverable.isPresent()) {
            ServiceDeliverable updatedDeliverable = deliverable.get();
            updatedDeliverable.setService(deliverableDetails.getService());
            updatedDeliverable.setName(deliverableDetails.getName());

            return ResponseEntity.ok(serviceDeliverableRepository.save(updatedDeliverable));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un entregable")
    public ResponseEntity<Void> deleteServiceDeliverable(@PathVariable Long id) {
        Optional<ServiceDeliverable> deliverable = serviceDeliverableRepository.findById(id);
        if (deliverable.isPresent()) {
            serviceDeliverableRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
