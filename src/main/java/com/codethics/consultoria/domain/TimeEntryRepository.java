package com.codethics.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    // Búsquedas básicas
    List<TimeEntry> findByUserId(Long userId);

    List<TimeEntry> findByProjectId(Long projectId);

    List<TimeEntry> findByTaskId(Long taskId);

    List<TimeEntry> findByStatus(TimeEntry.TimeEntryStatus status);

    List<TimeEntry> findByDate(LocalDate date);

    List<TimeEntry> findByUserIdAndDate(Long userId, LocalDate date);

    List<TimeEntry> findByProjectIdAndDate(Long projectId, LocalDate date);

    // Búsquedas por fechas
    List<TimeEntry> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<TimeEntry> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<TimeEntry> findByProjectIdAndDateBetween(Long projectId, LocalDate startDate, LocalDate endDate);

    // Búsquedas por facturación
    List<TimeEntry> findByBillable(Boolean billable);

    List<TimeEntry> findByBillableAndProjectId(Boolean billable, Long projectId);

    List<TimeEntry> findByBillableAndUserId(Boolean billable, Long userId);

    // Consultas JPQL personalizadas
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.status = :status")
    List<TimeEntry> findTimeEntriesByUserAndStatus(@Param("userId") Long userId,
            @Param("status") TimeEntry.TimeEntryStatus status);

    @Query("SELECT te FROM TimeEntry te WHERE te.project.id = :projectId AND te.status = :status")
    List<TimeEntry> findTimeEntriesByProjectAndStatus(@Param("projectId") Long projectId,
            @Param("status") TimeEntry.TimeEntryStatus status);

    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.date BETWEEN :startDate AND :endDate")
    List<TimeEntry> findTimeEntriesByUserAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT te FROM TimeEntry te WHERE te.project.id = :projectId AND te.date BETWEEN :startDate AND :endDate")
    List<TimeEntry> findTimeEntriesByProjectAndDateRange(@Param("projectId") Long projectId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT te FROM TimeEntry te WHERE te.billable = true AND te.status = 'COMPLETADO'")
    List<TimeEntry> findBillableCompletedTimeEntries();

    @Query("SELECT te FROM TimeEntry te WHERE te.billable = true AND te.project.id = :projectId")
    List<TimeEntry> findBillableTimeEntriesByProject(@Param("projectId") Long projectId);

    // Estadísticas y agregaciones
    @Query("SELECT SUM(te.durationHours) FROM TimeEntry te WHERE te.user.id = :userId AND te.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(te.durationHours) FROM TimeEntry te WHERE te.project.id = :projectId AND te.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByProjectAndDateRange(@Param("projectId") Long projectId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(te.totalAmount) FROM TimeEntry te WHERE te.billable = true AND te.project.id = :projectId")
    BigDecimal getTotalBillableAmountByProject(@Param("projectId") Long projectId);

    @Query("SELECT SUM(te.totalAmount) FROM TimeEntry te WHERE te.billable = true AND te.user.id = :userId")
    BigDecimal getTotalBillableAmountByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(te) FROM TimeEntry te WHERE te.user.id = :userId AND te.date = :date")
    Long countTimeEntriesByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(te) FROM TimeEntry te WHERE te.project.id = :projectId AND te.date = :date")
    Long countTimeEntriesByProjectAndDate(@Param("projectId") Long projectId, @Param("date") LocalDate date);

    @Query("SELECT AVG(te.durationHours) FROM TimeEntry te WHERE te.user.id = :userId AND te.date BETWEEN :startDate AND :endDate")
    BigDecimal getAverageHoursByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT AVG(te.durationHours) FROM TimeEntry te WHERE te.project.id = :projectId AND te.date BETWEEN :startDate AND :endDate")
    BigDecimal getAverageHoursByProjectAndDateRange(@Param("projectId") Long projectId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Búsquedas por tiempo
    @Query("SELECT te FROM TimeEntry te WHERE te.startTime >= :startTime AND te.endTime <= :endTime")
    List<TimeEntry> findTimeEntriesByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.startTime >= :startTime AND te.endTime <= :endTime")
    List<TimeEntry> findTimeEntriesByUserAndTimeRange(@Param("userId") Long userId,
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    // Eliminar todas las entradas de tiempo de un proyecto
    void deleteByProjectId(Long projectId);
}