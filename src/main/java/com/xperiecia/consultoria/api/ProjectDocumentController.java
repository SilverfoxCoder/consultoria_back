package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.application.ProjectDocumentService;
import com.xperiecia.consultoria.domain.ProjectDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Project Documents", description = "API para gestión de documentos de proyectos")
public class ProjectDocumentController {

    @Autowired
    private ProjectDocumentService projectDocumentService;

    @GetMapping("/projects/{projectId}/documents")
    @Operation(summary = "Listar documentos de un proyecto")
    public ResponseEntity<List<ProjectDocument>> getProjectDocuments(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectDocumentService.getDocumentsByProject(projectId));
    }

    @PostMapping("/projects/{projectId}/documents")
    @Operation(summary = "Subir un documento a un proyecto")
    public ResponseEntity<ProjectDocument> uploadDocument(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        // Fallback userId
        if (userId == null)
            userId = 1L;

        return ResponseEntity.ok(projectDocumentService.uploadDocument(projectId, file, userId));
    }

    @GetMapping("/documents/{documentId}/download")
    @Operation(summary = "Descargar un documento")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        Resource file = projectDocumentService.loadDocumentAsResource(documentId);
        ProjectDocument doc = projectDocumentService.getDocument(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/documents/{documentId}")
    @Operation(summary = "Eliminar un documento")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        projectDocumentService.deleteDocument(documentId);
        return ResponseEntity.ok().build();
    }
}
