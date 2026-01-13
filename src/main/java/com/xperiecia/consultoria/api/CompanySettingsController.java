package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.CompanySettings;
import com.xperiecia.consultoria.domain.CompanySettingsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/company-settings")
@Tag(name = "Company Settings", description = "API para gestión de configuración de empresa")
public class CompanySettingsController {

    @Autowired
    private CompanySettingsRepository companySettingsRepository;

    @GetMapping
    @Operation(summary = "Obtener todas las configuraciones de empresa")
    public List<CompanySettings> getAllCompanySettings() {
        return companySettingsRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener configuración de empresa por ID")
    public ResponseEntity<CompanySettings> getCompanySettingsById(@PathVariable Long id) {
        Optional<CompanySettings> settings = companySettingsRepository.findById(id);
        return settings.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva configuración de empresa")
    public CompanySettings createCompanySettings(@RequestBody CompanySettings companySettings) {
        return companySettingsRepository.save(companySettings);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar configuración de empresa")
    public ResponseEntity<CompanySettings> updateCompanySettings(@PathVariable Long id,
            @RequestBody CompanySettings settingsDetails) {
        Optional<CompanySettings> settings = companySettingsRepository.findById(id);
        if (settings.isPresent()) {
            CompanySettings updatedSettings = settings.get();
            updatedSettings.setName(settingsDetails.getName());
            updatedSettings.setLogo(settingsDetails.getLogo());
            updatedSettings.setAddress(settingsDetails.getAddress());
            updatedSettings.setPhone(settingsDetails.getPhone());
            updatedSettings.setEmail(settingsDetails.getEmail());
            updatedSettings.setTaxId(settingsDetails.getTaxId());
            updatedSettings.setWebsite(settingsDetails.getWebsite());
            updatedSettings.setLinkedin(settingsDetails.getLinkedin());
            updatedSettings.setTwitter(settingsDetails.getTwitter());
            updatedSettings.setFacebook(settingsDetails.getFacebook());

            return ResponseEntity.ok(companySettingsRepository.save(updatedSettings));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar configuración de empresa")
    public ResponseEntity<Void> deleteCompanySettings(@PathVariable Long id) {
        Optional<CompanySettings> settings = companySettingsRepository.findById(id);
        if (settings.isPresent()) {
            companySettingsRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
