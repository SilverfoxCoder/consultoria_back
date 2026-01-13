package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceDeliverableRepository extends JpaRepository<ServiceDeliverable, Long> {

    List<ServiceDeliverable> findByServiceId(Long serviceId);
}
