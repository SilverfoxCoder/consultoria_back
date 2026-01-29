package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Prospect;
import com.xperiecia.consultoria.domain.ProspectRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Arrays;

@Service
public class ProspectingService {

    private static final Logger logger = LoggerFactory.getLogger(ProspectingService.class);

    private final ProspectRepository prospectRepository;
    private final GoogleSearchProvider googleSearchProvider;

    // List of target cities in Spain
    private final List<String> TARGET_CITIES = Arrays.asList(
            "Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza", "Málaga");

    private int currentCityIndex = 0;

    public ProspectingService(ProspectRepository prospectRepository, GoogleSearchProvider googleSearchProvider) {
        this.prospectRepository = prospectRepository;
        this.googleSearchProvider = googleSearchProvider;
    }

    // Run daily at 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void runDailyProspecting() {
        logger.info("Starting daily prospecting task...");
        String city = TARGET_CITIES.get(currentCityIndex);
        performSearch(city);

        // Rotate city for next day
        currentCityIndex = (currentCityIndex + 1) % TARGET_CITIES.size();
    }

    public void triggerManualSearch(String city) {
        logger.info("Triggering manual prospecting for city: {}", city);
        performSearch(city);
    }

    private void performSearch(String city) {
        logger.info("Searching for prospects in {}", city);
        List<Prospect> prospects = googleSearchProvider.searchCompaniesWithoutWebsite(city);

        int addedCount = 0;
        for (Prospect p : prospects) {
            if (!prospectRepository.existsByCompanyNameAndCity(p.getCompanyName(), p.getCity())) {
                prospectRepository.save(p);
                addedCount++;
            }
        }
        logger.info("Prospecting completed for {}. Added {} new prospects.", city, addedCount);
    }

    public List<Prospect> getAllProspects() {
        return prospectRepository.findAll();
    }
}
