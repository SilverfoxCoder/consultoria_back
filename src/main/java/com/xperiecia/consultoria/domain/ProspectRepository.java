package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProspectRepository extends JpaRepository<Prospect, Long> {
    List<Prospect> findByStatus(String status);

    List<Prospect> findByCity(String city);

    boolean existsByCompanyNameAndCity(String companyName, String city);
}
