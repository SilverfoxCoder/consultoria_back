package com.xperiecia.consultoria.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> rootHealthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "message", "Application is running"));
    }
}
