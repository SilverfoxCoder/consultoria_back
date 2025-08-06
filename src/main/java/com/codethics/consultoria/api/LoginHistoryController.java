package com.codethics.consultoria.api;

import com.codethics.consultoria.domain.LoginHistory;
import com.codethics.consultoria.domain.LoginHistoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/login-history")
@Tag(name = "Login History", description = "API para gestión del historial de login")
public class LoginHistoryController {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @GetMapping
    @Operation(summary = "Obtener todo el historial de login")
    public List<LoginHistory> getAllLoginHistory() {
        return loginHistoryRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener entrada de historial por ID")
    public ResponseEntity<LoginHistory> getLoginHistoryById(@PathVariable Long id) {
        Optional<LoginHistory> loginHistory = loginHistoryRepository.findById(id);
        return loginHistory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener historial de login por usuario")
    public List<LoginHistory> getLoginHistoryByUser(@PathVariable Long userId) {
        return loginHistoryRepository.findByUser_Id(userId);
    }

    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Obtener historial de login reciente por usuario")
    public List<LoginHistory> getRecentLoginHistoryByUser(@PathVariable Long userId) {
        return loginHistoryRepository.findByUser_IdOrderByLoginAtDesc(userId);
    }

    @PostMapping
    @Operation(summary = "Crear nueva entrada en el historial de login")
    public LoginHistory createLoginHistory(@RequestBody LoginHistory loginHistory) {
        return loginHistoryRepository.save(loginHistory);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar entrada del historial de login")
    public ResponseEntity<LoginHistory> updateLoginHistory(@PathVariable Long id,
            @RequestBody LoginHistory historyDetails) {
        Optional<LoginHistory> loginHistory = loginHistoryRepository.findById(id);
        if (loginHistory.isPresent()) {
            LoginHistory updatedHistory = loginHistory.get();
            updatedHistory.setUser(historyDetails.getUser());
            updatedHistory.setLoginAt(historyDetails.getLoginAt());
            updatedHistory.setIp(historyDetails.getIp());
            updatedHistory.setDevice(historyDetails.getDevice());

            return ResponseEntity.ok(loginHistoryRepository.save(updatedHistory));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entrada del historial de login")
    public ResponseEntity<Void> deleteLoginHistory(@PathVariable Long id) {
        Optional<LoginHistory> loginHistory = loginHistoryRepository.findById(id);
        if (loginHistory.isPresent()) {
            loginHistoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}