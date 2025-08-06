package com.codethics.consultoria.api;

import com.codethics.consultoria.application.TimeEntryService;
import com.codethics.consultoria.dto.TimeEntryDTO;
import com.codethics.consultoria.dto.CreateTimeEntryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/time-entries")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @Autowired
    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    // CRUD básico
    @GetMapping
    public ResponseEntity<List<TimeEntryDTO>> getAllTimeEntries() {
        List<TimeEntryDTO> timeEntries = timeEntryService.getAllTimeEntries();
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntryDTO> getTimeEntryById(@PathVariable Long id) {
        TimeEntryDTO timeEntry = timeEntryService.getTimeEntryById(id);
        return ResponseEntity.ok(timeEntry);
    }

    @PostMapping
    public ResponseEntity<TimeEntryDTO> createTimeEntry(@Valid @RequestBody CreateTimeEntryRequest request) {
        TimeEntryDTO createdTimeEntry = timeEntryService.createTimeEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTimeEntry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntryDTO> updateTimeEntry(@PathVariable Long id, @Valid @RequestBody CreateTimeEntryRequest request) {
        TimeEntryDTO updatedTimeEntry = timeEntryService.updateTimeEntry(id, request);
        return ResponseEntity.ok(updatedTimeEntry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable Long id) {
        timeEntryService.deleteTimeEntry(id);
        return ResponseEntity.noContent().build();
    }

    // Consultas especializadas
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByUser(@PathVariable Long userId) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByUser(userId);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByProject(@PathVariable Long projectId) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByProject(projectId);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByTask(@PathVariable Long taskId) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByTask(taskId);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByStatus(@PathVariable String status) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByStatus(status);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByDate(@PathVariable LocalDate date) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByDate(date);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByUserAndDate(@PathVariable Long userId, @PathVariable LocalDate date) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByUserAndDate(userId, date);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/project/{projectId}/date/{date}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByProjectAndDate(@PathVariable Long projectId, @PathVariable LocalDate date) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByProjectAndDate(projectId, date);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/project/{projectId}/date-range")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByProjectAndDateRange(
            @PathVariable Long projectId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByProjectAndDateRange(projectId, startDate, endDate);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/billable/{billable}")
    public ResponseEntity<List<TimeEntryDTO>> getBillableTimeEntries(@PathVariable Boolean billable) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getBillableTimeEntries(billable);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/billable/{billable}/project/{projectId}")
    public ResponseEntity<List<TimeEntryDTO>> getBillableTimeEntriesByProject(@PathVariable Boolean billable, @PathVariable Long projectId) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getBillableTimeEntriesByProject(billable, projectId);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/billable/{billable}/user/{userId}")
    public ResponseEntity<List<TimeEntryDTO>> getBillableTimeEntriesByUser(@PathVariable Boolean billable, @PathVariable Long userId) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getBillableTimeEntriesByUser(billable, userId);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByUserAndStatus(@PathVariable Long userId, @PathVariable String status) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByUserAndStatus(userId, status);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByProjectAndStatus(@PathVariable Long projectId, @PathVariable String status) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getTimeEntriesByProjectAndStatus(projectId, status);
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/billable/completed")
    public ResponseEntity<List<TimeEntryDTO>> getBillableCompletedTimeEntries() {
        List<TimeEntryDTO> timeEntries = timeEntryService.getBillableCompletedTimeEntries();
        return ResponseEntity.ok(timeEntries);
    }

    @GetMapping("/billable/project/{projectId}")
    public ResponseEntity<List<TimeEntryDTO>> getBillableTimeEntriesByProject(@PathVariable Long projectId) {
        List<TimeEntryDTO> timeEntries = timeEntryService.getBillableTimeEntriesByProject(projectId);
        return ResponseEntity.ok(timeEntries);
    }

    // Estadísticas
    @GetMapping("/stats/total-hours/user/{userId}")
    public ResponseEntity<BigDecimal> getTotalHoursByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        BigDecimal totalHours = timeEntryService.getTotalHoursByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(totalHours);
    }

    @GetMapping("/stats/total-hours/project/{projectId}")
    public ResponseEntity<BigDecimal> getTotalHoursByProjectAndDateRange(
            @PathVariable Long projectId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        BigDecimal totalHours = timeEntryService.getTotalHoursByProjectAndDateRange(projectId, startDate, endDate);
        return ResponseEntity.ok(totalHours);
    }

    @GetMapping("/stats/total-amount/project/{projectId}")
    public ResponseEntity<BigDecimal> getTotalBillableAmountByProject(@PathVariable Long projectId) {
        BigDecimal totalAmount = timeEntryService.getTotalBillableAmountByProject(projectId);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/stats/total-amount/user/{userId}")
    public ResponseEntity<BigDecimal> getTotalBillableAmountByUser(@PathVariable Long userId) {
        BigDecimal totalAmount = timeEntryService.getTotalBillableAmountByUser(userId);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/stats/count/user/{userId}/date/{date}")
    public ResponseEntity<Long> getTimeEntryCountByUserAndDate(@PathVariable Long userId, @PathVariable LocalDate date) {
        Long count = timeEntryService.getTimeEntryCountByUserAndDate(userId, date);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/count/project/{projectId}/date/{date}")
    public ResponseEntity<Long> getTimeEntryCountByProjectAndDate(@PathVariable Long projectId, @PathVariable LocalDate date) {
        Long count = timeEntryService.getTimeEntryCountByProjectAndDate(projectId, date);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/average-hours/user/{userId}")
    public ResponseEntity<BigDecimal> getAverageHoursByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        BigDecimal averageHours = timeEntryService.getAverageHoursByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(averageHours);
    }

    @GetMapping("/stats/average-hours/project/{projectId}")
    public ResponseEntity<BigDecimal> getAverageHoursByProjectAndDateRange(
            @PathVariable Long projectId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        BigDecimal averageHours = timeEntryService.getAverageHoursByProjectAndDateRange(projectId, startDate, endDate);
        return ResponseEntity.ok(averageHours);
    }
} 