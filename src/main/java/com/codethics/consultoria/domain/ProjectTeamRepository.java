package com.codethics.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {

    List<ProjectTeam> findByProjectId(Long projectId);

    List<ProjectTeam> findByProjectIdAndRole(Long projectId, String role);

    // Eliminar todos los miembros del equipo de un proyecto
    void deleteByProjectId(Long projectId);
}