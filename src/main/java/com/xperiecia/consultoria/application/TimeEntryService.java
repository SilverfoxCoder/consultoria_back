package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.TimeEntry;
import com.xperiecia.consultoria.domain.TimeEntryRepository;
import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.domain.Project;
import com.xperiecia.consultoria.domain.ProjectRepository;
import com.xperiecia.consultoria.domain.Task;
import com.xperiecia.consultoria.domain.TaskRepository;
import com.xperiecia.consultoria.dto.TimeEntryDTO;
import com.xperiecia.consultoria.dto.CreateTimeEntryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TimeEntryService(TimeEntryRepository timeEntryRepository, UserRepository userRepository, 
                          ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    // CRUD básico
    public List<TimeEntryDTO> getAllTimeEntries() {
        return timeEntryRepository.findAll().stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TimeEntryDTO getTimeEntryById(Long id) {
        TimeEntry timeEntry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de tiempo no encontrado con ID: " + id));
        return TimeEntryDTO.fromEntity(timeEntry);
    }

    public TimeEntryDTO createTimeEntry(CreateTimeEntryRequest request) {
        validateTimeEntryRequest(request);
        
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setDate(request.getDate());
        timeEntry.setStartTime(request.getStartTime());
        timeEntry.setEndTime(request.getEndTime());
        timeEntry.setDurationHours(request.getDurationHours());
        timeEntry.setDescription(request.getDescription());
        timeEntry.setBillable(request.getBillable());
        timeEntry.setBillingRate(request.getBillingRate());
        timeEntry.setTotalAmount(request.getTotalAmount());

        // Establecer usuario
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUserId()));
        timeEntry.setUser(user);

        // Establecer proyecto si se proporciona
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId()));
            timeEntry.setProject(project);
        }

        // Establecer tarea si se proporciona
        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + request.getTaskId()));
            timeEntry.setTask(task);
        }

        // Establecer status
        if (request.getStatus() != null) {
            timeEntry.setStatus(TimeEntry.TimeEntryStatus.valueOf(request.getStatus()));
        }

        // Calcular duración si se proporcionan start y end time
        if (timeEntry.getStartTime() != null && timeEntry.getEndTime() != null) {
            timeEntry.calculateDuration();
        }

        // Calcular monto total si se proporcionan duración y tarifa
        if (timeEntry.getDurationHours() != null && timeEntry.getBillingRate() != null) {
            timeEntry.calculateTotalAmount();
        }

        TimeEntry savedTimeEntry = timeEntryRepository.save(timeEntry);
        return TimeEntryDTO.fromEntity(savedTimeEntry);
    }

    public TimeEntryDTO updateTimeEntry(Long id, CreateTimeEntryRequest request) {
        TimeEntry timeEntry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de tiempo no encontrado con ID: " + id));

        validateTimeEntryRequest(request);

        timeEntry.setDate(request.getDate());
        timeEntry.setStartTime(request.getStartTime());
        timeEntry.setEndTime(request.getEndTime());
        timeEntry.setDurationHours(request.getDurationHours());
        timeEntry.setDescription(request.getDescription());
        timeEntry.setBillable(request.getBillable());
        timeEntry.setBillingRate(request.getBillingRate());
        timeEntry.setTotalAmount(request.getTotalAmount());

        // Actualizar usuario
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUserId()));
        timeEntry.setUser(user);

        // Actualizar proyecto
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId()));
            timeEntry.setProject(project);
        }

        // Actualizar tarea
        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + request.getTaskId()));
            timeEntry.setTask(task);
        }

        // Actualizar status
        if (request.getStatus() != null) {
            timeEntry.setStatus(TimeEntry.TimeEntryStatus.valueOf(request.getStatus()));
        }

        // Calcular duración si se proporcionan start y end time
        if (timeEntry.getStartTime() != null && timeEntry.getEndTime() != null) {
            timeEntry.calculateDuration();
        }

        // Calcular monto total si se proporcionan duración y tarifa
        if (timeEntry.getDurationHours() != null && timeEntry.getBillingRate() != null) {
            timeEntry.calculateTotalAmount();
        }

        TimeEntry updatedTimeEntry = timeEntryRepository.save(timeEntry);
        return TimeEntryDTO.fromEntity(updatedTimeEntry);
    }

    public void deleteTimeEntry(Long id) {
        if (!timeEntryRepository.existsById(id)) {
            throw new RuntimeException("Registro de tiempo no encontrado con ID: " + id);
        }
        timeEntryRepository.deleteById(id);
    }

    // Consultas especializadas
    public List<TimeEntryDTO> getTimeEntriesByUser(Long userId) {
        return timeEntryRepository.findByUserId(userId).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByProject(Long projectId) {
        return timeEntryRepository.findByProjectId(projectId).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByTask(Long taskId) {
        return timeEntryRepository.findByTaskId(taskId).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByStatus(String status) {
        TimeEntry.TimeEntryStatus timeEntryStatus = TimeEntry.TimeEntryStatus.valueOf(status);
        return timeEntryRepository.findByStatus(timeEntryStatus).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByDate(LocalDate date) {
        return timeEntryRepository.findByDate(date).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByUserAndDate(Long userId, LocalDate date) {
        return timeEntryRepository.findByUserIdAndDate(userId, date).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByProjectAndDate(Long projectId, LocalDate date) {
        return timeEntryRepository.findByProjectIdAndDate(projectId, date).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.findByDateBetween(startDate, endDate).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.findByUserIdAndDateBetween(userId, startDate, endDate).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByProjectAndDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.findByProjectIdAndDateBetween(projectId, startDate, endDate).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getBillableTimeEntries(Boolean billable) {
        return timeEntryRepository.findByBillable(billable).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getBillableTimeEntriesByProject(Boolean billable, Long projectId) {
        return timeEntryRepository.findByBillableAndProjectId(billable, projectId).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getBillableTimeEntriesByUser(Boolean billable, Long userId) {
        return timeEntryRepository.findByBillableAndUserId(billable, userId).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByUserAndStatus(Long userId, String status) {
        TimeEntry.TimeEntryStatus timeEntryStatus = TimeEntry.TimeEntryStatus.valueOf(status);
        return timeEntryRepository.findTimeEntriesByUserAndStatus(userId, timeEntryStatus).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getTimeEntriesByProjectAndStatus(Long projectId, String status) {
        TimeEntry.TimeEntryStatus timeEntryStatus = TimeEntry.TimeEntryStatus.valueOf(status);
        return timeEntryRepository.findTimeEntriesByProjectAndStatus(projectId, timeEntryStatus).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getBillableCompletedTimeEntries() {
        return timeEntryRepository.findBillableCompletedTimeEntries().stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeEntryDTO> getBillableTimeEntriesByProject(Long projectId) {
        return timeEntryRepository.findBillableTimeEntriesByProject(projectId).stream()
                .map(TimeEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Estadísticas
    public BigDecimal getTotalHoursByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.getTotalHoursByUserAndDateRange(userId, startDate, endDate);
    }

    public BigDecimal getTotalHoursByProjectAndDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.getTotalHoursByProjectAndDateRange(projectId, startDate, endDate);
    }

    public BigDecimal getTotalBillableAmountByProject(Long projectId) {
        return timeEntryRepository.getTotalBillableAmountByProject(projectId);
    }

    public BigDecimal getTotalBillableAmountByUser(Long userId) {
        return timeEntryRepository.getTotalBillableAmountByUser(userId);
    }

    public Long getTimeEntryCountByUserAndDate(Long userId, LocalDate date) {
        return timeEntryRepository.countTimeEntriesByUserAndDate(userId, date);
    }

    public Long getTimeEntryCountByProjectAndDate(Long projectId, LocalDate date) {
        return timeEntryRepository.countTimeEntriesByProjectAndDate(projectId, date);
    }

    public BigDecimal getAverageHoursByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.getAverageHoursByUserAndDateRange(userId, startDate, endDate);
    }

    public BigDecimal getAverageHoursByProjectAndDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.getAverageHoursByProjectAndDateRange(projectId, startDate, endDate);
    }

    // Validaciones
    private void validateTimeEntryRequest(CreateTimeEntryRequest request) {
        if (request.getUserId() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }

        if (request.getDate() == null) {
            throw new RuntimeException("La fecha es obligatoria");
        }

        if (request.getStartTime() == null) {
            throw new RuntimeException("La hora de inicio es obligatoria");
        }

        if (request.getEndTime() != null && request.getStartTime() != null) {
            if (request.getEndTime().isBefore(request.getStartTime())) {
                throw new RuntimeException("La hora de fin no puede ser anterior a la hora de inicio");
            }
        }

        if (request.getBillingRate() != null && request.getBillingRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("La tarifa de facturación debe ser mayor o igual a 0");
        }

        if (request.getDurationHours() != null && request.getDurationHours().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("La duración en horas debe ser mayor o igual a 0");
        }
    }
} 
