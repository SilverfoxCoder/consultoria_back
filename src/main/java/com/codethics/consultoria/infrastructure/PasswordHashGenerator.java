package com.codethics.consultoria.infrastructure;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hashedPassword = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);

        // Verificar que el hash es válido
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("Hash válido: " + matches);
    }
}