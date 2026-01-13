package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Buscar por número de factura
    Invoice findByNumber(String number);

    // Buscar por cliente
    List<Invoice> findByClientId(Long clientId);

    // Buscar por estado
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);

    // Buscar por cliente y estado
    List<Invoice> findByClientIdAndStatus(Long clientId, Invoice.InvoiceStatus status);

    // Buscar facturas vencidas
    @Query("SELECT i FROM Invoice i WHERE i.issuedAt < :today AND i.status NOT IN ('PAGADA', 'CANCELADA')")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);

    // Buscar facturas por rango de fechas de emisión
    List<Invoice> findByIssuedAtBetween(LocalDate startDate, LocalDate endDate);

    // Buscar facturas por rango de fechas de pago
    List<Invoice> findByPaidAtBetween(LocalDate startDate, LocalDate endDate);

    // Buscar facturas por rango de montos
    List<Invoice> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // Buscar facturas por términos de pago
    List<Invoice> findByPaymentTerms(String paymentTerms);

    // Buscar facturas por número (búsqueda parcial)
    List<Invoice> findByNumberContainingIgnoreCase(String number);

    // Buscar facturas por notas (búsqueda parcial)
    List<Invoice> findByNotesContainingIgnoreCase(String notes);

    // Calcular total por cliente
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.client.id = :clientId AND i.status = 'PAGADA'")
    BigDecimal calculateTotalPaidByClient(@Param("clientId") Long clientId);

    // Calcular total pendiente por cliente
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.client.id = :clientId AND i.status IN ('ENVIADA', 'VENCIDA')")
    BigDecimal calculateTotalPendingByClient(@Param("clientId") Long clientId);

    // Calcular total por estado
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.status = :status")
    BigDecimal calculateTotalByStatus(@Param("status") Invoice.InvoiceStatus status);

    // Calcular total por rango de fechas
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.issuedAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Contar facturas por estado
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    Long countByStatus(@Param("status") Invoice.InvoiceStatus status);

    // Contar facturas por cliente
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.client.id = :clientId")
    Long countByClient(@Param("clientId") Long clientId);

    // Contar facturas vencidas
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.issuedAt < :today AND i.status NOT IN ('PAGADA', 'CANCELADA')")
    Long countOverdueInvoices(@Param("today") LocalDate today);

    // Estadísticas por estado
    @Query("SELECT i.status, COUNT(i), SUM(i.amount) FROM Invoice i GROUP BY i.status")
    List<Object[]> getStatisticsByStatus();

    // Estadísticas por cliente
    @Query("SELECT i.client.name, COUNT(i), SUM(i.amount) FROM Invoice i GROUP BY i.client.id, i.client.name")
    List<Object[]> getStatisticsByClient();

    // Top clientes por facturación
    @Query("SELECT i.client.name, SUM(i.amount) FROM Invoice i WHERE i.status = 'PAGADA' GROUP BY i.client.id, i.client.name ORDER BY SUM(i.amount) DESC")
    List<Object[]> getTopClientsByRevenue(@Param("limit") int limit);

    // Buscar facturas creadas en un rango de fechas
    List<Invoice> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar facturas actualizadas en un rango de fechas
    List<Invoice> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar facturas por monto mínimo
    List<Invoice> findByAmountGreaterThan(BigDecimal amount);

    // Buscar facturas por monto máximo
    List<Invoice> findByAmountLessThan(BigDecimal amount);

    // Buscar facturas por año
    @Query("SELECT i FROM Invoice i WHERE YEAR(i.issuedAt) = :year")
    List<Invoice> findByYear(@Param("year") int year);

    // Buscar facturas por mes y año
    @Query("SELECT i FROM Invoice i WHERE YEAR(i.issuedAt) = :year AND MONTH(i.issuedAt) = :month")
    List<Invoice> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Calcular total por año
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE YEAR(i.issuedAt) = :year")
    BigDecimal calculateTotalByYear(@Param("year") int year);

    // Calcular total por mes y año
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE YEAR(i.issuedAt) = :year AND MONTH(i.issuedAt) = :month")
    BigDecimal calculateTotalByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
