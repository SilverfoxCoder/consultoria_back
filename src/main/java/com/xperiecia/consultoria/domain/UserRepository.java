package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    /**
     * Contar usuarios creados entre dos fechas
     */
    long countByRegisteredAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Contar usuarios por estado
     */
    long countByStatus(String status);

    /**
     * Buscar usuarios por estado
     */
    @Query("SELECT u FROM User u WHERE u.status = :status")
    java.util.List<User> findByStatus(@Param("status") String status);

    java.util.List<User> findByRole(String role);

    long countByRole(String role);

    long countByRoleAndLastContactBetween(String role, LocalDateTime start, LocalDateTime end);

    long countByRoleAndStatus(String role, String status);
}
