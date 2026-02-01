package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.ProjectTeam;
import com.xperiecia.consultoria.domain.ProjectTeamRepository;
import com.xperiecia.consultoria.domain.ProjectRepository;
import com.xperiecia.consultoria.dto.ProjectTeamDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de equipos de proyecto
 * 
 * Este controlador maneja las operaciones CRUD para los miembros
 * del equipo de cada proyecto, incluyendo validaciones de existencia
 * de proyectos y roles de equipo.
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/project-teams")
@Tag(name = "Project Teams", description = "API para gestión de equipos de proyecto")
public class ProjectTeamController {

    /**
     * Repositorio para operaciones de base de datos de equipos de proyecto
     */
    @Autowired
    private ProjectTeamRepository projectTeamRepository;

    /**
     * Repositorio para operaciones de base de datos de proyectos
     * Se usa para validar que el proyecto existe antes de crear/actualizar miembros
     */
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private com.xperiecia.consultoria.domain.UserRepository userRepository;

    /**
     * Obtiene todos los miembros de equipos de proyecto del sistema
     * 
     * @return Lista de todos los miembros de equipo en formato DTO
     */
    @GetMapping
    @Operation(summary = "Obtener todos los equipos de proyecto")
    public List<ProjectTeamDTO> getAllProjectTeams() {
        return projectTeamRepository.findAll().stream()
                .map(ProjectTeamDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un miembro específico del equipo por su ID
     * 
     * @param id ID del miembro del equipo a buscar
     * @return Miembro del equipo encontrado o 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un miembro del equipo por ID")
    public ResponseEntity<ProjectTeamDTO> getProjectTeamById(@PathVariable Long id) {
        Optional<ProjectTeam> teamMember = projectTeamRepository.findById(id);
        return teamMember.map(t -> ResponseEntity.ok(ProjectTeamDTO.fromEntity(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene todos los miembros del equipo de un proyecto específico
     * 
     * @param projectId ID del proyecto
     * @return Lista de miembros del equipo del proyecto
     */
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Obtener equipo por proyecto")
    public List<ProjectTeamDTO> getProjectTeamByProject(@PathVariable Long projectId) {
        return projectTeamRepository.findByProjectId(projectId).stream()
                .map(ProjectTeamDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene miembros del equipo filtrados por proyecto y rol
     * 
     * @param projectId ID del proyecto
     * @param role      Rol específico a buscar (ej: "Desarrollador", "Diseñador")
     * @return Lista de miembros del equipo con el rol especificado
     */
    @GetMapping("/project/{projectId}/role/{role}")
    @Operation(summary = "Obtener miembros del equipo por proyecto y rol")
    public List<ProjectTeamDTO> getProjectTeamByProjectAndRole(@PathVariable Long projectId,
            @PathVariable String role) {
        return projectTeamRepository.findByProjectIdAndRole(projectId, role).stream()
                .map(ProjectTeamDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo miembro del equipo de proyecto
     * 
     * Valida que:
     * - El ID del proyecto sea obligatorio
     * - El proyecto exista en la base de datos
     * 
     * @param projectTeamDTO Datos del nuevo miembro del equipo
     * @return Miembro del equipo creado
     * @throws RuntimeException si el proyecto no existe o el ID es nulo
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo miembro del equipo")
    public ProjectTeamDTO createProjectTeam(@RequestBody ProjectTeamDTO projectTeamDTO) {
        // Validar que el ID del proyecto sea obligatorio
        if (projectTeamDTO.getProjectId() == null) {
            throw new RuntimeException("El ID del proyecto es obligatorio");
        }

        // Verificar que el proyecto existe
        Project project = projectRepository.findById(projectTeamDTO.getProjectId())
                .orElseThrow(
                        () -> new RuntimeException("Proyecto no encontrado con ID: " + projectTeamDTO.getProjectId()));

        // Crear nueva entidad ProjectTeam
        ProjectTeam projectTeam = new ProjectTeam();
        projectTeam.setProject(project);
        projectTeam.setName(projectTeamDTO.getName());
        projectTeam.setRole(projectTeamDTO.getRole());

        // Asignar usuario si se proporciona el ID
        if (projectTeamDTO.getUserId() != null) {
            com.xperiecia.consultoria.domain.User user = userRepository.findById(projectTeamDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException(
                            "Usuario no encontrado con ID: " + projectTeamDTO.getUserId()));
            projectTeam.setUser(user);
            projectTeam.setName(user.getName()); // Sobreescribimos el nombre con el del usuario real
        }

        // Guardar en base de datos y retornar DTO
        ProjectTeam savedProjectTeam = projectTeamRepository.save(projectTeam);
        return ProjectTeamDTO.fromEntity(savedProjectTeam);
    }

    /**
     * Actualiza un miembro existente del equipo de proyecto
     * 
     * Permite actualizar:
     * - El proyecto al que pertenece (si se proporciona nuevo projectId)
     * - El nombre del miembro
     * - El rol del miembro
     * 
     * @param id          ID del miembro del equipo a actualizar
     * @param teamDetails Nuevos datos del miembro del equipo
     * @return Miembro del equipo actualizado o 404 si no existe
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un miembro del equipo")
    public ResponseEntity<ProjectTeamDTO> updateProjectTeam(@PathVariable Long id,
            @RequestBody ProjectTeamDTO teamDetails) {
        Optional<ProjectTeam> teamMember = projectTeamRepository.findById(id);
        if (teamMember.isPresent()) {
            ProjectTeam updatedMember = teamMember.get();

            // Actualizar proyecto si se proporciona un nuevo projectId
            if (teamDetails.getProjectId() != null) {
                Project project = projectRepository.findById(teamDetails.getProjectId())
                        .orElseThrow(() -> new RuntimeException(
                                "Proyecto no encontrado con ID: " + teamDetails.getProjectId()));
                updatedMember.setProject(project);
            }

            // Actualizar usuario si se proporciona ID (o limpiar si es necesario, pero aquí
            // solo añadimos)
            if (teamDetails.getUserId() != null) {
                com.xperiecia.consultoria.domain.User user = userRepository.findById(teamDetails.getUserId())
                        .orElseThrow(() -> new RuntimeException(
                                "Usuario no encontrado con ID: " + teamDetails.getUserId()));
                updatedMember.setUser(user);
                updatedMember.setName(user.getName());
            } else if (teamDetails.getName() != null) {
                // Solo actualizar nombre si no hay usuario vinculado o si se quiere cambiar
                // manualmente
                // (Aunque si hay usuario vinculado, debería prevalecer su nombre, por ahora lo
                // dejamos flexible)
                updatedMember.setName(teamDetails.getName());
            }

            // Actualizar rol
            if (teamDetails.getRole() != null) {
                updatedMember.setRole(teamDetails.getRole());
            }

            // Guardar cambios y retornar DTO
            ProjectTeam savedProjectTeam = projectTeamRepository.save(updatedMember);
            return ResponseEntity.ok(ProjectTeamDTO.fromEntity(savedProjectTeam));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Elimina un miembro del equipo de proyecto
     * 
     * @param id ID del miembro del equipo a eliminar
     * @return 200 si se eliminó correctamente, 404 si no existe
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un miembro del equipo")
    public ResponseEntity<Void> deleteProjectTeam(@PathVariable Long id) {
        Optional<ProjectTeam> teamMember = projectTeamRepository.findById(id);
        return teamMember.map(t -> {
            projectTeamRepository.deleteById(id);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
