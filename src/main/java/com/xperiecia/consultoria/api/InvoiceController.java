package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Invoice;
import com.xperiecia.consultoria.domain.InvoiceRepository;
import com.xperiecia.consultoria.dto.InvoiceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Invoices", description = "API para gestión de facturas")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping
    @Operation(summary = "Obtener todas las facturas")
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una factura por ID")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        return invoice.map(i -> ResponseEntity.ok(InvoiceDTO.fromEntity(i)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener facturas por cliente")
    public List<InvoiceDTO> getInvoicesByClient(@PathVariable Long clientId) {
        return invoiceRepository.findByClient_Id(clientId).stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener facturas por estado")
    public List<InvoiceDTO> getInvoicesByStatus(@PathVariable String status) {
        return invoiceRepository.findByStatus(Invoice.InvoiceStatus.valueOf(status.toUpperCase())).stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva factura")
    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceDTO.toEntity();
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceDTO.fromEntity(savedInvoice);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una factura")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDetails) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            Invoice updatedInvoice = invoice.get();
            updatedInvoice.setNumber(invoiceDetails.getNumber());
            updatedInvoice.setIssuedAt(invoiceDetails.getIssuedAt());
            updatedInvoice.setPaidAt(invoiceDetails.getPaidAt());
            updatedInvoice.setAmount(invoiceDetails.getAmount());
            if (invoiceDetails.getStatus() != null) {
                updatedInvoice.setStatus(Invoice.InvoiceStatus.valueOf(invoiceDetails.getStatus()));
            }
            updatedInvoice.setPaymentTerms(invoiceDetails.getPaymentTerms());
            updatedInvoice.setNotes(invoiceDetails.getNotes());

            Invoice savedInvoice = invoiceRepository.save(updatedInvoice);
            return ResponseEntity.ok(InvoiceDTO.fromEntity(savedInvoice));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una factura")
    public ResponseEntity<Map<String, Object>> deleteInvoice(@PathVariable Long id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            try {
                invoiceRepository.deleteById(id);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Factura eliminada correctamente");
                response.put("id", id);
                response.put("success", true);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Error al eliminar la factura: " + e.getMessage());
                response.put("error", e.getMessage());
                response.put("success", false);
                response.put("id", id);
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Factura no encontrada");
            response.put("success", false);
            response.put("id", id);
            return ResponseEntity.notFound().build();
        }
    }
}
