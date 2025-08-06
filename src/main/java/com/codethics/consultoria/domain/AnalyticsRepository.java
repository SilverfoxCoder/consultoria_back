package com.codethics.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    List<Analytics> findByClientId(Long clientId);

    Optional<Analytics> findByClientIdAndMonthPeriod(Long clientId, String monthPeriod);

    List<Analytics> findByMonthPeriod(String monthPeriod);
}