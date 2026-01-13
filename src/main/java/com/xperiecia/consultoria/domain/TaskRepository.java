package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Búsquedas básicas
    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssignedToId(Long userId);

    List<Task> findByStatus(Task.TaskStatus status);

    List<Task> findByPriority(Task.TaskPriority priority);

    List<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status);

    // Búsquedas por fechas
    @Query("SELECT t FROM Task t WHERE t.dueDate < :today")
    List<Task> findByDueDateBefore(@Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Task> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // Consultas JPQL personalizadas
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.status = :status")
    List<Task> findTasksByUserAndStatus(@Param("userId") Long userId, @Param("status") Task.TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.dueDate < :date")
    List<Task> findOverdueTasksByProject(@Param("projectId") Long projectId, @Param("date") LocalDate date);

    // Métodos para conteo por estado
    long countByStatus(Task.TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    Long countByProjectAndStatus(@Param("projectId") Long projectId, @Param("status") Task.TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.estimatedHours > t.actualHours AND t.status != 'COMPLETADA'")
    List<Task> findTasksUnderEstimatedHours();

    @Query("SELECT t FROM Task t WHERE t.actualHours > t.estimatedHours")
    List<Task> findTasksOverEstimatedHours();

    // Estadísticas
    @Query("SELECT AVG(t.actualHours) FROM Task t WHERE t.project.id = :projectId AND t.status = 'COMPLETADA'")
    Double getAverageActualHoursByProject(@Param("projectId") Long projectId);

    @Query("SELECT SUM(t.actualHours) FROM Task t WHERE t.project.id = :projectId")
    Double getTotalActualHoursByProject(@Param("projectId") Long projectId);

    // Eliminar todas las tareas de un proyecto
    void deleteByProjectId(Long projectId);
}
