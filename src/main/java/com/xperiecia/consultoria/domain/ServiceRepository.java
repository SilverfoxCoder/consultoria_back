package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByClient_Id(Long clientId);

    List<Service> findByStatus(String status);

    List<Service> findByType(String type);

    List<Service> findByClient_IdAndStatus(Long clientId, String status);
}
