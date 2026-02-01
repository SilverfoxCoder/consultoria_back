package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.Analytics;
import com.xperiecia.consultoria.domain.AnalyticsRepository;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.dto.AnalyticsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "API para gestión de analíticas y KPIs")
public class AnalyticsController {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private UserRepository userRepository;

    private AnalyticsDTO convertToDTO(Analytics analytics) {
        return new AnalyticsDTO(
                analytics.getId(),
                analytics.getClient() != null ? analytics.getClient().getId() : null,
                analytics.getMonthPeriod(),
                analytics.getTotalSpent(),
                analytics.getActiveProjects(),
                analytics.getOpenTickets(),
                analytics.getAvgResponseTime());
    }

    private Analytics convertToEntity(AnalyticsDTO dto) {
        Analytics analytics = new Analytics();
        analytics.setId(dto.getId());
        analytics.setMonthPeriod(dto.getMonthPeriod());
        analytics.setTotalSpent(dto.getTotalSpent());
        analytics.setActiveProjects(dto.getActiveProjects());
        analytics.setOpenTickets(dto.getOpenTickets());
        analytics.setAvgResponseTime(dto.getAvgResponseTime());

        if (dto.getClientId() != null) {
            userRepository.findById(dto.getClientId().longValue()).ifPresent(analytics::setClient);
        }

        return analytics;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las analíticas")
    public List<AnalyticsDTO> getAllAnalytics() {
        return analyticsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener analítica por ID")
    public ResponseEntity<AnalyticsDTO> getAnalyticsById(@PathVariable long id) {
        Optional<Analytics> analytics = analyticsRepository.findById(id);
        return analytics.map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener analíticas por cliente")
    public List<AnalyticsDTO> getAnalyticsByClient(@PathVariable long clientId) {
        // Updated to use findByClient_Id
        return analyticsRepository.findByClient_Id(clientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/month/{monthPeriod}")
    @Operation(summary = "Obtener analíticas por mes")
    public List<AnalyticsDTO> getAnalyticsByMonth(@PathVariable String monthPeriod) {
        return analyticsRepository.findByMonthPeriod(monthPeriod).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/client/{clientId}/month/{monthPeriod}")
    @Operation(summary = "Obtener analítica específica por cliente y mes")
    public ResponseEntity<AnalyticsDTO> getAnalyticsByClientAndMonth(@PathVariable long clientId,
            @PathVariable String monthPeriod) {
        // Updated to use findByClient_IdAndMonthPeriod
        Optional<Analytics> analytics = analyticsRepository.findByClient_IdAndMonthPeriod(clientId, monthPeriod);
        return analytics.map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva analítica")
    public AnalyticsDTO createAnalytics(@RequestBody AnalyticsDTO analyticsDTO) {
        Analytics analytics = convertToEntity(analyticsDTO);
        Analytics savedAnalytics = analyticsRepository.save(analytics);
        return convertToDTO(savedAnalytics);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar analítica")
    public ResponseEntity<AnalyticsDTO> updateAnalytics(@PathVariable long id, @RequestBody AnalyticsDTO analyticsDTO) {
        Optional<Analytics> analytics = analyticsRepository.findById(id);
        if (analytics.isPresent()) {
            Analytics updatedAnalytics = analytics.get();
            updatedAnalytics.setMonthPeriod(analyticsDTO.getMonthPeriod());
            updatedAnalytics.setTotalSpent(analyticsDTO.getTotalSpent());
            updatedAnalytics.setActiveProjects(analyticsDTO.getActiveProjects());
            updatedAnalytics.setOpenTickets(analyticsDTO.getOpenTickets());
            updatedAnalytics.setAvgResponseTime(analyticsDTO.getAvgResponseTime());

            if (analyticsDTO.getClientId() != null) {
                userRepository.findById(analyticsDTO.getClientId().longValue()).ifPresent(updatedAnalytics::setClient);
            }

            Analytics savedAnalytics = analyticsRepository.save(updatedAnalytics);
            return ResponseEntity.ok(convertToDTO(savedAnalytics));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar analítica")
    public ResponseEntity<Void> deleteAnalytics(@PathVariable long id) {
        Optional<Analytics> analytics = analyticsRepository.findById(id);
        if (analytics.isPresent()) {
            analyticsRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
