package com.xperiecia.consultoria.dto;

import lombok.Data;

/**
 * DTO para solicitudes de registro de usuarios
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Data
public class RegisterUserRequest {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
    private String role;
} 
