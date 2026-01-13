package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByStatus(String status);
    
    /**
     * Contar clientes activos en un período de tiempo
     * (clientes que tuvieron actividad - último contacto en el período)
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.lastContact BETWEEN :start AND :end")
    long countActiveInPeriod(@Param("start") java.time.LocalDate start, @Param("end") java.time.LocalDate end);

    /**
     * Buscar cliente por email
     */
    Optional<Client> findByEmail(String email);
}
