package com.codethics.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {

    // Normalmente solo habrá una configuración de empresa
    // Por eso no necesitamos métodos adicionales específicos
}