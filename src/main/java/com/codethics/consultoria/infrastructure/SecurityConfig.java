package com.codethics.consultoria.infrastructure;

import com.codethics.consultoria.domain.User;
import com.codethics.consultoria.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            System.out.println("=== DEBUG: Buscando usuario con email: " + username);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            System.out.println("=== DEBUG: Usuario encontrado: " + user.getName());
            System.out.println("=== DEBUG: Password hash en BD: " + user.getPasswordHash());
            System.out.println("=== DEBUG: Role: " + user.getRole());
            System.out.println("=== DEBUG: Status: " + user.getStatus());

            // Verificar si el hash es válido
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isValidHash = encoder.matches("admin123", user.getPasswordHash());
            System.out.println("=== DEBUG: Hash válido para 'admin123': " + isValidHash);

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPasswordHash())
                    .roles(user.getRole().toUpperCase())
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.and()) // Habilitar CORS usando configuración por defecto
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitir todos los requests sin autenticación
                );
        return http.build();
    }
}