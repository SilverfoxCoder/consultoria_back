package com.codethics.consultoria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "project_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status")
    private String status;

    @Column(name = "assignee")
    private String assignee;
}