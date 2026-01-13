package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClientId(Long clientId);

    List<Project> findByStatus(Project.ProjectStatus status);

    List<Project> findByPriority(Project.ProjectPriority priority);

    // Buscar proyectos activos (en progreso o planificación)
    @Query("SELECT p FROM Project p WHERE p.status IN ('EN_PROGRESO', 'PLANIFICACION')")
    List<Project> findActiveProjects();

    // Proyectos con progreso bajo
    @Query("SELECT p FROM Project p WHERE p.progress < 25")
    List<Project> findProjectsWithLowProgress();

    // Proyectos que exceden el presupuesto
    @Query("SELECT p FROM Project p WHERE p.spent > p.budget")
    List<Project> findProjectsOverBudget();

    // Métodos para conteo por estado
    long countByStatus(Project.ProjectStatus status);

    // Suma total de presupuestos
    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM Project p")
    BigDecimal getTotalBudget();

    // Suma total de gastos
    @Query("SELECT COALESCE(SUM(p.spent), 0) FROM Project p")
    BigDecimal getTotalSpent();
}
