package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByUser_Id(Long userId);

    List<LoginHistory> findByUser_IdOrderByLoginAtDesc(Long userId);
    
    /**
     * Contar logins únicos (usuarios únicos) en un rango de fechas
     */
    @Query("SELECT COUNT(DISTINCT lh.user.id) FROM LoginHistory lh WHERE lh.loginAt BETWEEN :start AND :end")
    long countUniqueUsersByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * Contar total de logins en un rango de fechas
     */
    long countByLoginAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Buscar logins en un rango de fechas
     */
    List<LoginHistory> findByLoginAtBetween(LocalDateTime start, LocalDateTime end);
}
