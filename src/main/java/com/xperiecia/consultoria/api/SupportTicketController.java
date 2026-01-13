package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.SupportTicket;
import com.xperiecia.consultoria.domain.SupportTicketRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/support-tickets")
@Tag(name = "Support Tickets", description = "API para gestión de tickets de soporte")
public class SupportTicketController {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los tickets de soporte")
    public List<SupportTicket> getAllSupportTickets() {
        return supportTicketRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un ticket de soporte por ID")
    public ResponseEntity<SupportTicket> getSupportTicketById(@PathVariable Long id) {
        Optional<SupportTicket> ticket = supportTicketRepository.findById(id);
        return ticket.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener tickets por cliente")
    public List<SupportTicket> getSupportTicketsByClient(@PathVariable Long clientId) {
        return supportTicketRepository.findByClientId(clientId);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener tickets por estado")
    public List<SupportTicket> getSupportTicketsByStatus(@PathVariable String status) {
        return supportTicketRepository.findByStatus(status);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Obtener tickets por prioridad")
    public List<SupportTicket> getSupportTicketsByPriority(@PathVariable String priority) {
        return supportTicketRepository.findByPriority(priority);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo ticket de soporte")
    public SupportTicket createSupportTicket(@RequestBody SupportTicket supportTicket) {
        return supportTicketRepository.save(supportTicket);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un ticket de soporte")
    public ResponseEntity<SupportTicket> updateSupportTicket(@PathVariable Long id,
            @RequestBody SupportTicket ticketDetails) {
        Optional<SupportTicket> ticket = supportTicketRepository.findById(id);
        if (ticket.isPresent()) {
            SupportTicket updatedTicket = ticket.get();
            updatedTicket.setClient(ticketDetails.getClient());
            updatedTicket.setTitle(ticketDetails.getTitle());
            updatedTicket.setDescription(ticketDetails.getDescription());
            updatedTicket.setStatus(ticketDetails.getStatus());
            updatedTicket.setCreatedAt(ticketDetails.getCreatedAt());
            updatedTicket.setClosedAt(ticketDetails.getClosedAt());
            updatedTicket.setPriority(ticketDetails.getPriority());

            return ResponseEntity.ok(supportTicketRepository.save(updatedTicket));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un ticket de soporte")
    public ResponseEntity<Void> deleteSupportTicket(@PathVariable Long id) {
        Optional<SupportTicket> ticket = supportTicketRepository.findById(id);
        if (ticket.isPresent()) {
            supportTicketRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
