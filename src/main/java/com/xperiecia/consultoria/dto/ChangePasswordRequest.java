package com.xperiecia.consultoria.dto;

import lombok.Data;

/**
 * DTO para solicitudes de cambio de contraseña
 * 
 * @author CodEthics Team
 * @version 1.0
 */
@Data
public class ChangePasswordRequest {
    private Long userId;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
} 
