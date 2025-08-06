package com.codethics.consultoria.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        error.put("path", request.getDescription(false));
        error.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex,
            WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        errors.put("timestamp", System.currentTimeMillis());
        errors.put("path", request.getDescription(false));
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex,
            WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        error.put("path", request.getDescription(false));
        error.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error interno del servidor: " + ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        error.put("path", request.getDescription(false));
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}