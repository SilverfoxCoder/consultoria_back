package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.ProjectDocument;
import com.xperiecia.consultoria.domain.ProjectDocumentRepository;
import com.xperiecia.consultoria.domain.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProjectDocumentService {

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<ProjectDocument> getDocumentsByProject(Long projectId) {
        return projectDocumentRepository.findByProjectId(projectId);
    }

    @Transactional
    public ProjectDocument uploadDocument(Long projectId, MultipartFile file, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

        String storedFilename = fileStorageService.store(file);

        ProjectDocument document = new ProjectDocument();
        document.setProject(project);
        document.setFilename(storedFilename);
        document.setOriginalFilename(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setPath(storedFilename); // Storing filename as path for now
        document.setUploadedBy(userId);

        return projectDocumentRepository.save(document);
    }

    public Resource loadDocumentAsResource(Long documentId) {
        ProjectDocument document = projectDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id " + documentId));
        return fileStorageService.loadAsResource(document.getFilename());
    }

    public ProjectDocument getDocument(Long documentId) {
        return projectDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id " + documentId));
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        ProjectDocument document = projectDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id " + documentId));

        fileStorageService.delete(document.getFilename());
        projectDocumentRepository.delete(document);
    }
}
