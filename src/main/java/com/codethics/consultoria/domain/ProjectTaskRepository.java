package com.codethics.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProjectId(Long projectId);

    List<ProjectTask> findByProjectIdAndStatus(Long projectId, String status);

    List<ProjectTask> findByAssignee(String assignee);
}