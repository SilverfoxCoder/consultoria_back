package com.codethics.consultoria.application;

import com.codethics.consultoria.domain.InvoiceItem;
import com.codethics.consultoria.domain.Invoice;
import com.codethics.consultoria.domain.InvoiceItemRepository;
import com.codethics.consultoria.domain.InvoiceRepository;
import com.codethics.consultoria.dto.InvoiceItemDTO;
import com.codethics.consultoria.dto.CreateInvoiceItemRequest;
import com.codethics.consultoria.exception.ResourceNotFoundException;
import com.codethics.consultoria.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceItemService {

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    // CRUD Operations
    public List<InvoiceItemDTO> getAllInvoiceItems() {
        return invoiceItemRepository.findAll().stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public InvoiceItemDTO getInvoiceItemById(Long id) {
        InvoiceItem invoiceItem = invoiceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de factura no encontrado con ID: " + id));
        return InvoiceItemDTO.fromEntity(invoiceItem);
    }

    public InvoiceItemDTO createInvoiceItem(CreateInvoiceItemRequest request) {
        // Validar que la factura existe
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + request.getInvoiceId()));

        // Crear el ítem
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setName(request.getName());
        invoiceItem.setDescription(request.getDescription());
        invoiceItem.setQuantity(request.getQuantity());
        invoiceItem.setUnitPrice(request.getUnitPrice());
        invoiceItem.setType(InvoiceItem.ItemType.valueOf(request.getType()));
        invoiceItem.setStatus(InvoiceItem.ItemStatus.valueOf(request.getStatus()));
        invoiceItem.setTaxRate(request.getTaxRate());
        invoiceItem.setDiscountPercentage(request.getDiscountPercentage());

        // Calcular totales
        invoiceItem.calculateTotals();

        InvoiceItem savedItem = invoiceItemRepository.save(invoiceItem);
        return InvoiceItemDTO.fromEntity(savedItem);
    }

    public InvoiceItemDTO updateInvoiceItem(Long id, CreateInvoiceItemRequest request) {
        InvoiceItem existingItem = invoiceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de factura no encontrado con ID: " + id));

        // Validar que la factura existe si se está cambiando
        if (!existingItem.getInvoice().getId().equals(request.getInvoiceId())) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + request.getInvoiceId()));
            existingItem.setInvoice(invoice);
        }

        existingItem.setName(request.getName());
        existingItem.setDescription(request.getDescription());
        existingItem.setQuantity(request.getQuantity());
        existingItem.setUnitPrice(request.getUnitPrice());
        existingItem.setType(InvoiceItem.ItemType.valueOf(request.getType()));
        existingItem.setStatus(InvoiceItem.ItemStatus.valueOf(request.getStatus()));
        existingItem.setTaxRate(request.getTaxRate());
        existingItem.setDiscountPercentage(request.getDiscountPercentage());

        // Calcular totales
        existingItem.calculateTotals();

        InvoiceItem updatedItem = invoiceItemRepository.save(existingItem);
        return InvoiceItemDTO.fromEntity(updatedItem);
    }

    public void deleteInvoiceItem(Long id) {
        if (!invoiceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ítem de factura no encontrado con ID: " + id);
        }
        invoiceItemRepository.deleteById(id);
    }

    // Specialized Queries
    public List<InvoiceItemDTO> getInvoiceItemsByInvoice(Long invoiceId) {
        return invoiceItemRepository.findByInvoiceId(invoiceId).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getActiveInvoiceItemsByInvoice(Long invoiceId) {
        return invoiceItemRepository.findActiveItemsByInvoice(invoiceId).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getInvoiceItemsByType(String type) {
        InvoiceItem.ItemType itemType = InvoiceItem.ItemType.valueOf(type.toUpperCase());
        return invoiceItemRepository.findByType(itemType).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getInvoiceItemsByStatus(String status) {
        InvoiceItem.ItemStatus itemStatus = InvoiceItem.ItemStatus.valueOf(status.toUpperCase());
        return invoiceItemRepository.findByStatus(itemStatus).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getInvoiceItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return invoiceItemRepository.findByUnitPriceBetween(minPrice, maxPrice).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> searchInvoiceItemsByName(String name) {
        return invoiceItemRepository.findByNameContainingIgnoreCase(name).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> searchInvoiceItemsByDescription(String description) {
        return invoiceItemRepository.findByDescriptionContainingIgnoreCase(description).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getInvoiceItemsByClient(Long clientId) {
        return invoiceItemRepository.findByClientId(clientId).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getTopInvoiceItemsByAmount(int limit) {
        return invoiceItemRepository.findTopItemsByAmount(limit).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getInvoiceItemsWithDiscount(BigDecimal discountPercentage) {
        return invoiceItemRepository.findByDiscountPercentageGreaterThan(discountPercentage).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getInvoiceItemsWithTax(BigDecimal taxRate) {
        return invoiceItemRepository.findByTaxRateGreaterThan(taxRate).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceItemDTO> getInvoiceItemsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceItemRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(InvoiceItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Statistics and Calculations
    public BigDecimal calculateTotalByInvoice(Long invoiceId) {
        BigDecimal total = invoiceItemRepository.calculateTotalByInvoice(invoiceId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalTaxByInvoice(Long invoiceId) {
        BigDecimal totalTax = invoiceItemRepository.calculateTotalTaxByInvoice(invoiceId);
        return totalTax != null ? totalTax : BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalDiscountByInvoice(Long invoiceId) {
        BigDecimal totalDiscount = invoiceItemRepository.calculateTotalDiscountByInvoice(invoiceId);
        return totalDiscount != null ? totalDiscount : BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalByClient(Long clientId) {
        BigDecimal total = invoiceItemRepository.calculateTotalByClient(clientId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long countInvoiceItemsByInvoice(Long invoiceId) {
        return invoiceItemRepository.countByInvoice(invoiceId);
    }

    public Long countActiveInvoiceItemsByInvoice(Long invoiceId) {
        return invoiceItemRepository.countActiveByInvoice(invoiceId);
    }

    public List<Object[]> getStatisticsByType() {
        return invoiceItemRepository.getStatisticsByType();
    }

    public List<Object[]> getStatisticsByStatus() {
        return invoiceItemRepository.getStatisticsByStatus();
    }
} 