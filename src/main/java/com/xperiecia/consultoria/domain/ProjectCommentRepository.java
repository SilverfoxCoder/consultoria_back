package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long> {
    List<ProjectComment> findByProjectId(Long projectId);

    List<ProjectComment> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
