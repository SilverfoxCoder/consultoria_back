package com.codethics.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Budget
 * 
 * Proporciona métodos para acceder y gestionar los presupuestos
 * en la base de datos.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Buscar presupuestos por cliente
     * 
     * @param clientId ID del cliente
     * @return Lista de presupuestos del cliente
     */
    List<Budget> findByClientId(Long clientId);

    /**
     * Buscar presupuestos por estado
     * 
     * @param status Estado del presupuesto
     * @return Lista de presupuestos con el estado especificado
     */
    List<Budget> findByStatus(Budget.BudgetStatus status);

    /**
     * Contar presupuestos por estado
     * 
     * @param status Estado del presupuesto
     * @return Número de presupuestos con el estado especificado
     */
    long countByStatus(Budget.BudgetStatus status);

    /**
     * Buscar presupuestos pendientes
     * 
     * @return Lista de presupuestos pendientes
     */
    List<Budget> findByStatusOrderByCreatedAtDesc(Budget.BudgetStatus status);

    /**
     * Buscar presupuestos por cliente y estado
     * 
     * @param clientId ID del cliente
     * @param status   Estado del presupuesto
     * @return Lista de presupuestos del cliente con el estado especificado
     */
    List<Budget> findByClientIdAndStatus(Long clientId, Budget.BudgetStatus status);

    /**
     * Obtener presupuestos ordenados por fecha de creación (más recientes primero)
     * 
     * @return Lista de presupuestos ordenados
     */
    List<Budget> findAllByOrderByCreatedAtDesc();

    /**
     * Buscar presupuestos por cliente ordenados por fecha de creación
     * 
     * @param clientId ID del cliente
     * @return Lista de presupuestos del cliente ordenados
     */
    List<Budget> findByClientIdOrderByCreatedAtDesc(Long clientId);

    /**
     * Obtener estadísticas de presupuestos
     * 
     * @return Array con [total, pendientes, en_revision, aprobados, rechazados]
     */
    @Query("SELECT COUNT(b) FROM Budget b")
    long getTotalBudgets();

    @Query("SELECT COUNT(b) FROM Budget b WHERE b.status = 'PENDIENTE'")
    long getPendingBudgets();

    @Query("SELECT COUNT(b) FROM Budget b WHERE b.status = 'EN_REVISION'")
    long getInReviewBudgets();

    @Query("SELECT COUNT(b) FROM Budget b WHERE b.status = 'APROBADO'")
    long getApprovedBudgets();

    @Query("SELECT COUNT(b) FROM Budget b WHERE b.status = 'RECHAZADO'")
    long getRejectedBudgets();
    
    /**
     * Contar presupuestos creados entre dos fechas
     */
    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}