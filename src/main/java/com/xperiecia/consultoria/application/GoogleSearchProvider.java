package com.xperiecia.consultoria.application;

import com.xperiecia.consultoria.domain.Prospect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Imports removed as they are unused or fully qualified in code

@Component
public class GoogleSearchProvider {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchProvider.class);
    private static final String API_KEY = "c8eb61498cdcae5c9a7d4d43ef19acf2b1ea031b"; // Corrected Key from User

    public List<Prospect> searchCompaniesWithoutWebsite(String city) {
        List<Prospect> results = new ArrayList<>();
        String query = "reformas en " + city;

        try {
            logger.info("Executing Serper.dev API search for: {}", query);

            String requestBody = String.format("{\"q\": \"%s\", \"gl\": \"es\", \"hl\": \"es\", \"num\": 20}", query);

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://google.serper.dev/search"))
                    .header("X-API-KEY", API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("Serper API returned error: {} - Body: {}", response.statusCode(), response.body());
                System.out.println("SERPER ERROR BODY: " + response.body()); // Force visible log
                logger.info("ACTIVATING DEMO MODE (Fallback due to API Error)");
                return getDemoProspects(city);
            }

            String responseBody = response.body();
            logger.info("Serper API Response: {}", responseBody);
            System.out.println("SERPER RAW JSON: " + responseBody); // Force visible log

            // Parse JSON response
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(responseBody);

            // 1. Parse "places" (Local Pack - High Priority)
            com.fasterxml.jackson.databind.JsonNode places = root.path("places");
            if (places.isArray()) {
                logger.info("Found {} items in 'places' (Local Pack)", places.size());
                System.out.println("FOUND PLACES: " + places.size());
                for (com.fasterxml.jackson.databind.JsonNode result : places) {
                    try {
                        String title = result.path("title").asText();
                        String address = result.path("address").asText("");
                        String phoneNumber = result.has("phoneNumber") ? result.path("phoneNumber").asText() : null;
                        String website = result.has("website") ? result.path("website").asText() : "";

                        // Filter out generic results or chains
                        if (isGenericTitle(title)) {
                            logger.debug("Skipping generic title in uses places: {}", title);
                            continue;
                        }

                        // If website is empty OR it's a social/directory/business.site -> It's a
                        // prospect
                        boolean hasNoProWebsite = website.isEmpty() || isDirectoryOrSocialMedia(website);

                        if (hasNoProWebsite) {
                            Prospect p = new Prospect();
                            p.setCompanyName(cleanTitle(title));
                            p.setSource("Serper (Maps): " + (website.isEmpty() ? "No Website" : getDomain(website)));
                            p.setCity(city);
                            p.setAddress(address);
                            p.setStatus("NEW");
                            p.setFoundAt(LocalDateTime.now());
                            p.setWebsite(website);
                            p.setPhone(phoneNumber != null ? phoneNumber : "Not found");

                            results.add(p);
                            logger.info("Added Prospect from Maps: {}", title);
                        } else {
                            logger.debug("Skipping Maps result with pro website: {} ({})", title, website);
                        }
                    } catch (Exception e) {
                        logger.warn("Error parsing Serper Place item", e);
                    }
                }
            } else {
                logger.info("No 'places' array in response.");
            }

            // 2. Parse "organic" (Web Results) - Lower priority, often directories
            com.fasterxml.jackson.databind.JsonNode organic = root.path("organic");
            if (organic.isArray()) {
                logger.info("Found {} items in 'organic'", organic.size());
                for (com.fasterxml.jackson.databind.JsonNode result : organic) {
                    try {
                        String title = result.path("title").asText();
                        String link = result.path("link").asText();
                        String snippet = result.path("snippet").asText();

                        if (link.isEmpty() || title.isEmpty())
                            continue;

                        // Strict filter for organic to avoid "Top 10" lists being added as companies
                        if (isGenericTitle(title)) {
                            continue;
                        }

                        if (isDirectoryOrSocialMedia(link)) {
                            Prospect p = new Prospect();
                            p.setCompanyName(cleanTitle(title));
                            p.setSource("Serper (Organic): " + getDomain(link));
                            p.setCity(city);
                            p.setStatus("NEW");
                            p.setFoundAt(LocalDateTime.now());
                            p.setWebsite("");

                            String phone = extractPhoneNumber(snippet);
                            if (phone == null)
                                phone = extractPhoneNumber(title);
                            p.setPhone(phone != null ? phone : "Not found");

                            results.add(p);
                            logger.info("Added Prospect from Organic: {}", title);
                        }
                    } catch (Exception e) {
                        logger.warn("Error parsing Serper Organic item", e);
                    }
                }
            }

            if (results.isEmpty()) {
                logger.warn("No relevant prospects found in Serper response.");
                logger.debug("Full Response for debug: {}", response.body());
                return getDemoProspects(city);
            }

        } catch (Exception e) {
            logger.error("Error performing Serper API search", e);
            return getDemoProspects(city);
        }

        if (results.isEmpty()) {
            return getDemoProspects(city);
        }

        return results;
    }

    private boolean isGenericTitle(String title) {
        String lower = title.toLowerCase();
        return lower.contains("mejores") ||
                lower.contains("top 10") ||
                lower.contains("listado de") ||
                lower.contains("directorio") ||
                lower.contains("empresas de reformas en");
    }

    private List<Prospect> getDemoProspects(String city) {
        List<Prospect> demos = new ArrayList<>();
        Prospect demo1 = new Prospect();
        demo1.setCompanyName("Reformas " + city + " Express (Demo)");
        demo1.setCity(city);
        demo1.setPhone("600 123 456");
        demo1.setSource("DEMO_MODE (API 403/Error)");
        demo1.setStatus("NEW");
        demo1.setWebsite("https://www.facebook.com/reformas" + city);
        demo1.setFoundAt(LocalDateTime.now());
        demos.add(demo1);

        Prospect demo2 = new Prospect();
        demo2.setCompanyName("Construcciones García " + city + " (Demo)");
        demo2.setCity(city);
        demo2.setPhone("912 345 678");
        demo2.setSource("DEMO_MODE (API 403/Error)");
        demo2.setStatus("NEW");
        demo2.setWebsite("https://reformasgarcia.business.site");
        demo2.setFoundAt(LocalDateTime.now());
        demos.add(demo2);

        return demos;
    }

    private boolean isDirectoryOrSocialMedia(String url) {
        String[] directories = {
                "facebook.com", "instagram.com", "linkedin.com", "paginasamarillas.es",
                "yelp.es", "tripadvisor.es", "habitissimo.es", "cronoshare.com",
                "milanuncios.com", "vulka.es", "axesor.es", "einforma.com", "citiservi.es",
                "paxinasgalegas.es", "qdq.com", "business.site", "google.com"
        };
        for (String dir : directories) {
            if (url.toLowerCase().contains(dir))
                return true;
        }
        return false;
    }

    private String getDomain(String url) {
        try {
            return java.net.URI.create(url).getHost();
        } catch (Exception e) {
            return url;
        }
    }

    private String cleanTitle(String title) {
        String[] suffixes = { " - Inicio", " - Home", " - Google Maps", " | Facebook", " • Instagram",
                " - Business Site", " - Negocio.site", " en Repsol", " - Páginas Amarillas",
                " - Habitissimo", " - Tripadvisor", " - Yelp" };

        String cleaned = title;
        for (String suffix : suffixes) {
            if (cleaned.contains(suffix)) {
                cleaned = cleaned.split(java.util.regex.Pattern.quote(suffix))[0];
            }
        }

        if (cleaned.startsWith("Perfiles de "))
            cleaned = cleaned.replace("Perfiles de ", "");
        if (cleaned.startsWith("Ver perfiles de "))
            cleaned = cleaned.replace("Ver perfiles de ", "");

        if (cleaned.contains(" - "))
            cleaned = cleaned.split(" - ")[0];
        if (cleaned.contains(" | "))
            cleaned = cleaned.split(" \\| ")[0];

        return cleaned.trim();
    }

    private String extractPhoneNumber(String text) {
        if (text == null)
            return null;
        java.util.regex.Pattern p = java.util.regex.Pattern
                .compile("(?:(?:00)?34)?[ -]?(?:[6789]\\d{2}[ -]?\\d{3}[ -]?\\d{3})");
        java.util.regex.Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(0).trim();
        }
        return null;
    }
}
