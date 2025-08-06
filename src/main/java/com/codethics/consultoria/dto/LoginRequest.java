package com.codethics.consultoria.dto;

import lombok.Data;

/**
 * DTO para solicitudes de login
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
} 