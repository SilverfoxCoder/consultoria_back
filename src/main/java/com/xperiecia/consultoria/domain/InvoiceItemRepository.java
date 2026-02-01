package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    // Buscar por factura
    List<InvoiceItem> findByInvoiceId(Long invoiceId);

    // Buscar por tipo de ítem
    List<InvoiceItem> findByType(InvoiceItem.ItemType type);

    // Buscar por estado
    List<InvoiceItem> findByStatus(InvoiceItem.ItemStatus status);

    // Buscar por factura y estado
    List<InvoiceItem> findByInvoiceIdAndStatus(Long invoiceId, InvoiceItem.ItemStatus status);

    // Buscar por rango de precios
    List<InvoiceItem> findByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Buscar por cantidad
    List<InvoiceItem> findByQuantityGreaterThan(Integer quantity);

    // Buscar por nombre (búsqueda parcial)
    List<InvoiceItem> findByNameContainingIgnoreCase(String name);

    // Buscar por descripción (búsqueda parcial)
    List<InvoiceItem> findByDescriptionContainingIgnoreCase(String description);

    // Buscar ítems activos por factura
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId AND ii.status = 'ACTIVO'")
    List<InvoiceItem> findActiveItemsByInvoice(@Param("invoiceId") Long invoiceId);

    // Calcular total por factura
    @Query("SELECT SUM(ii.totalAmount) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId AND ii.status = 'ACTIVO'")
    BigDecimal calculateTotalByInvoice(@Param("invoiceId") Long invoiceId);

    // Calcular total de impuestos por factura
    @Query("SELECT SUM(ii.taxAmount) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId AND ii.status = 'ACTIVO'")
    BigDecimal calculateTotalTaxByInvoice(@Param("invoiceId") Long invoiceId);

    // Calcular total de descuentos por factura
    @Query("SELECT SUM(ii.discountAmount) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId AND ii.status = 'ACTIVO'")
    BigDecimal calculateTotalDiscountByInvoice(@Param("invoiceId") Long invoiceId);

    // Estadísticas por tipo
    @Query("SELECT ii.type, COUNT(ii), SUM(ii.totalAmount) FROM InvoiceItem ii WHERE ii.status = 'ACTIVO' GROUP BY ii.type")
    List<Object[]> getStatisticsByType();

    // Estadísticas por estado
    @Query("SELECT ii.status, COUNT(ii), SUM(ii.totalAmount) FROM InvoiceItem ii GROUP BY ii.status")
    List<Object[]> getStatisticsByStatus();

    // Buscar ítems creados en un rango de fechas
    List<InvoiceItem> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar ítems actualizados en un rango de fechas
    List<InvoiceItem> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Contar ítems por factura
    @Query("SELECT COUNT(ii) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId")
    Long countByInvoice(@Param("invoiceId") Long invoiceId);

    // Contar ítems activos por factura
    @Query("SELECT COUNT(ii) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId AND ii.status = 'ACTIVO'")
    Long countActiveByInvoice(@Param("invoiceId") Long invoiceId);

    // Buscar ítems con descuento
    List<InvoiceItem> findByDiscountPercentageGreaterThan(BigDecimal discountPercentage);

    // Buscar ítems con impuestos
    List<InvoiceItem> findByTaxRateGreaterThan(BigDecimal taxRate);

    // Top ítems por monto total
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.status = 'ACTIVO' ORDER BY ii.totalAmount DESC")
    List<InvoiceItem> findTopItemsByAmount(@Param("limit") int limit);

    // Buscar ítems por cliente (a través de la factura)
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoice.client.id = :clientId")
    List<InvoiceItem> findByClient_Id(@Param("clientId") Long clientId);

    // Calcular total por cliente
    @Query("SELECT SUM(ii.totalAmount) FROM InvoiceItem ii WHERE ii.invoice.client.id = :clientId AND ii.status = 'ACTIVO'")
    BigDecimal calculateTotalByClient(@Param("clientId") Long clientId);
}
