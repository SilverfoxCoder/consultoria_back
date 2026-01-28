package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.application.ProspectingService;
import com.xperiecia.consultoria.domain.Prospect;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prospects")
@Tag(name = "Prospects", description = "API para gestión de prospectos recopilados por el bot")
public class ProspectController {

    private final ProspectingService prospectingService;

    public ProspectController(ProspectingService prospectingService) {
        this.prospectingService = prospectingService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los prospectos")
    public List<Prospect> getAllProspects() {
        return prospectingService.getAllProspects();
    }

    @PostMapping("/search/trigger")
    @Operation(summary = "Ejecutar manualmente la búsqueda de prospectos")
    public ResponseEntity<String> triggerSearch(@RequestParam(defaultValue = "Madrid") String city) {
        prospectingService.triggerManualSearch(city);
        return ResponseEntity
                .ok("Búsqueda iniciada para " + city + ". Revisa los logs o la lista de prospectos en unos momentos.");
    }
}
