package com.codethics.consultoria.api;

import com.codethics.consultoria.domain.UserPreference;
import com.codethics.consultoria.domain.UserPreferenceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-preferences")
@Tag(name = "User Preferences", description = "API para gestión de preferencias de usuario")
public class UserPreferenceController {

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @GetMapping
    @Operation(summary = "Obtener todas las preferencias de usuario")
    public List<UserPreference> getAllUserPreferences() {
        return userPreferenceRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener preferencias de usuario por ID")
    public ResponseEntity<UserPreference> getUserPreferenceById(@PathVariable Long id) {
        Optional<UserPreference> preference = userPreferenceRepository.findById(id);
        return preference.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener preferencias por usuario")
    public ResponseEntity<UserPreference> getUserPreferenceByUserId(@PathVariable Long userId) {
        Optional<UserPreference> preference = userPreferenceRepository.findByUserId(userId);
        return preference.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevas preferencias de usuario")
    public UserPreference createUserPreference(@RequestBody UserPreference userPreference) {
        return userPreferenceRepository.save(userPreference);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar preferencias de usuario")
    public ResponseEntity<UserPreference> updateUserPreference(@PathVariable Long id,
            @RequestBody UserPreference preferenceDetails) {
        Optional<UserPreference> preference = userPreferenceRepository.findById(id);
        if (preference.isPresent()) {
            UserPreference updatedPreference = preference.get();
            updatedPreference.setUser(preferenceDetails.getUser());
            updatedPreference.setLanguage(preferenceDetails.getLanguage());
            updatedPreference.setTimezone(preferenceDetails.getTimezone());
            updatedPreference.setCurrency(preferenceDetails.getCurrency());
            updatedPreference.setNotificationsEmail(preferenceDetails.getNotificationsEmail());
            updatedPreference.setNotificationsSms(preferenceDetails.getNotificationsSms());
            updatedPreference.setNotificationsPush(preferenceDetails.getNotificationsPush());

            return ResponseEntity.ok(userPreferenceRepository.save(updatedPreference));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar preferencias de usuario")
    public ResponseEntity<Void> deleteUserPreference(@PathVariable Long id) {
        Optional<UserPreference> preference = userPreferenceRepository.findById(id);
        if (preference.isPresent()) {
            userPreferenceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}