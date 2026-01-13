package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.application.InvoiceItemService;
import com.xperiecia.consultoria.dto.InvoiceItemDTO;
import com.xperiecia.consultoria.dto.CreateInvoiceItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invoice-items")
public class InvoiceItemController {

    @Autowired
    private InvoiceItemService invoiceItemService;

    // CRUD Operations
    @GetMapping
    public ResponseEntity<List<InvoiceItemDTO>> getAllInvoiceItems() {
        List<InvoiceItemDTO> items = invoiceItemService.getAllInvoiceItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceItemDTO> getInvoiceItemById(@PathVariable Long id) {
        InvoiceItemDTO item = invoiceItemService.getInvoiceItemById(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<InvoiceItemDTO> createInvoiceItem(@Valid @RequestBody CreateInvoiceItemRequest request) {
        InvoiceItemDTO createdItem = invoiceItemService.createInvoiceItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceItemDTO> updateInvoiceItem(@PathVariable Long id, @Valid @RequestBody CreateInvoiceItemRequest request) {
        InvoiceItemDTO updatedItem = invoiceItemService.updateInvoiceItem(id, request);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoiceItem(@PathVariable Long id) {
        invoiceItemService.deleteInvoiceItem(id);
        return ResponseEntity.noContent().build();
    }

    // Specialized Queries
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsByInvoice(@PathVariable Long invoiceId) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsByInvoice(invoiceId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/invoice/{invoiceId}/active")
    public ResponseEntity<List<InvoiceItemDTO>> getActiveInvoiceItemsByInvoice(@PathVariable Long invoiceId) {
        List<InvoiceItemDTO> items = invoiceItemService.getActiveInvoiceItemsByInvoice(invoiceId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsByType(@PathVariable String type) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsByType(type);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsByStatus(@PathVariable String status) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsByStatus(status);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<InvoiceItemDTO>> searchInvoiceItemsByName(@RequestParam String name) {
        List<InvoiceItemDTO> items = invoiceItemService.searchInvoiceItemsByName(name);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search/description")
    public ResponseEntity<List<InvoiceItemDTO>> searchInvoiceItemsByDescription(@RequestParam String description) {
        List<InvoiceItemDTO> items = invoiceItemService.searchInvoiceItemsByDescription(description);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsByClient(@PathVariable Long clientId) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsByClient(clientId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/top-by-amount")
    public ResponseEntity<List<InvoiceItemDTO>> getTopInvoiceItemsByAmount(@RequestParam(defaultValue = "10") int limit) {
        List<InvoiceItemDTO> items = invoiceItemService.getTopInvoiceItemsByAmount(limit);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/with-discount")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsWithDiscount(@RequestParam BigDecimal discountPercentage) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsWithDiscount(discountPercentage);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/with-tax")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsWithTax(@RequestParam BigDecimal taxRate) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsWithTax(taxRate);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<InvoiceItemDTO>> getInvoiceItemsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<InvoiceItemDTO> items = invoiceItemService.getInvoiceItemsByDateRange(startDate, endDate);
        return ResponseEntity.ok(items);
    }

    // Statistics and Calculations
    @GetMapping("/invoice/{invoiceId}/total")
    public ResponseEntity<BigDecimal> calculateTotalByInvoice(@PathVariable Long invoiceId) {
        BigDecimal total = invoiceItemService.calculateTotalByInvoice(invoiceId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/invoice/{invoiceId}/total-tax")
    public ResponseEntity<BigDecimal> calculateTotalTaxByInvoice(@PathVariable Long invoiceId) {
        BigDecimal totalTax = invoiceItemService.calculateTotalTaxByInvoice(invoiceId);
        return ResponseEntity.ok(totalTax);
    }

    @GetMapping("/invoice/{invoiceId}/total-discount")
    public ResponseEntity<BigDecimal> calculateTotalDiscountByInvoice(@PathVariable Long invoiceId) {
        BigDecimal totalDiscount = invoiceItemService.calculateTotalDiscountByInvoice(invoiceId);
        return ResponseEntity.ok(totalDiscount);
    }

    @GetMapping("/client/{clientId}/total")
    public ResponseEntity<BigDecimal> calculateTotalByClient(@PathVariable Long clientId) {
        BigDecimal total = invoiceItemService.calculateTotalByClient(clientId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/invoice/{invoiceId}/count")
    public ResponseEntity<Long> countInvoiceItemsByInvoice(@PathVariable Long invoiceId) {
        Long count = invoiceItemService.countInvoiceItemsByInvoice(invoiceId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/invoice/{invoiceId}/count-active")
    public ResponseEntity<Long> countActiveInvoiceItemsByInvoice(@PathVariable Long invoiceId) {
        Long count = invoiceItemService.countActiveInvoiceItemsByInvoice(invoiceId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/by-type")
    public ResponseEntity<List<Object[]>> getStatisticsByType() {
        List<Object[]> statistics = invoiceItemService.getStatisticsByType();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/by-status")
    public ResponseEntity<List<Object[]>> getStatisticsByStatus() {
        List<Object[]> statistics = invoiceItemService.getStatisticsByStatus();
        return ResponseEntity.ok(statistics);
    }
} 
