package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    List<Analytics> findByClient_Id(Long clientId);

    Optional<Analytics> findByClient_IdAndMonthPeriod(Long clientId, String monthPeriod);

    List<Analytics> findByMonthPeriod(String monthPeriod);
}
