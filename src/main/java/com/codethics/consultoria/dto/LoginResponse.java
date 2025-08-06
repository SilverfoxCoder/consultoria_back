package com.codethics.consultoria.dto;

import lombok.Data;
import java.util.Map;

/**
 * DTO para respuestas de login
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private Map<String, Object> user;
} 