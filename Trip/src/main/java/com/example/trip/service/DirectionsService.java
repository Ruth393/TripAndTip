package com.example.trip.service;

import com.example.trip.dto.DirectionsRequest;
import com.example.trip.dto.DirectionsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DirectionsService {

    private static final Set<String> ALLOWED_PROFILES = Set.of(
            "foot-walking", "foot-hiking", "driving-car", "cycling-regular"
    );

    @Value("${ors.api.key:}")
    private String apiKey;

    @Value("${ors.base.url:https://api.openrouteservice.org/v2/directions}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public DirectionsResponse getDirections(DirectionsRequest req) {

        System.out.println("===== DIRECTIONS CALLED =====");
        System.out.println("API KEY LENGTH: " + (apiKey == null ? "NULL" : apiKey.length()));
        System.out.println("API KEY PREFIX: " + (apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) : apiKey));
        System.out.println("BASE URL: " + baseUrl);
        System.out.println("==============================");

        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "חסר API key בשרת - בדקי את application.properties");
        }

        String profile = ALLOWED_PROFILES.contains(req.getProfile()) ? req.getProfile() : "foot-walking";
        String url = baseUrl + "/" + profile + "/geojson";

        Map<String, Object> body = Map.of(
                "coordinates", List.of(
                        List.of(req.getStartLng(), req.getStartLat()),
                        List.of(req.getEndLng(), req.getEndLat())
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/geo+json, application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            return parseResponse(response);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("===== ORS HTTP ERROR =====");
            System.err.println("STATUS: " + e.getStatusCode());
            System.err.println("BODY: " + e.getResponseBodyAsString());
            System.err.println("===========================");
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ORS error: " + e.getResponseBodyAsString());

        } catch (RestClientException e) {
            System.err.println("===== ORS CONNECTION ERROR =====");
            e.printStackTrace();
            System.err.println("=================================");
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "לא ניתן היה להתחבר ל-ORS: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("===== UNEXPECTED ERROR =====");
            e.printStackTrace();
            System.err.println("=============================");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "שגיאה לא צפויה: " + e.getMessage());
        }
    }

    private DirectionsResponse parseResponse(Map<String, Object> response) {
        List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
        if (features == null || features.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "לא נמצא מסלול בין הנקודות");
        }

        Map<String, Object> feature = features.get(0);
        Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
        List<Map<String, Object>> segments = (List<Map<String, Object>>) properties.get("segments");
        Map<String, Object> summary = segments.get(0);

        double distance = ((Number) summary.get("distance")).doubleValue();
        double duration = ((Number) summary.get("duration")).doubleValue();

        Map<String, Object> geometry = (Map<String, Object>) feature.get("geometry");
        List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

        List<double[]> path = new ArrayList<>();
        for (List<Double> coord : coordinates) {
            path.add(new double[]{ coord.get(1), coord.get(0) });
        }

        return new DirectionsResponse(distance, duration, path);
    }
}