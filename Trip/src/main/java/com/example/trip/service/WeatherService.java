package com.example.trip.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final RestClient restClient = RestClient.create();

    public String getWeatherForCity(String city) {
        double[] coords = geocodeCity(city);
        if (coords == null) {
            return "לא נמצאה עיר בשם \"" + city + "\" במאגר מזג האוויר.";
        }
        return getWeatherForCoordinates(coords[0], coords[1], city);
    }

    @SuppressWarnings("unchecked")
    public String getWeatherForCoordinates(double lat, double lon, String label) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}" +
                            "&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max,weathercode" +
                            "&timezone=auto&forecast_days=3", lat, lon)
                    .retrieve()
                    .body(Map.class);

            if (response == null || !response.containsKey("daily")) {
                return "לא הצלחתי להביא נתוני מזג אוויר עבור " + label + ".";
            }

            Map<String, Object> daily = (Map<String, Object>) response.get("daily");
            List<String> dates = (List<String>) daily.get("time");
            List<Number> maxTemps = (List<Number>) daily.get("temperature_2m_max");
            List<Number> minTemps = (List<Number>) daily.get("temperature_2m_min");
            List<Number> precipProb = (List<Number>) daily.get("precipitation_probability_max");
            List<Number> codes = (List<Number>) daily.get("weathercode");

            StringBuilder sb = new StringBuilder("תחזית ל-" + label + ":\n");
            for (int i = 0; i < dates.size(); i++) {
                sb.append(dates.get(i)).append(": ")
                        .append(minTemps.get(i)).append("°-").append(maxTemps.get(i)).append("°C, ")
                        .append("סיכוי משקעים ").append(precipProb.get(i)).append("%, ")
                        .append(weatherCodeToHebrew(codes.get(i).intValue()))
                        .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "שגיאה בקבלת מזג אוויר: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    private double[] geocodeCity(String city) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1&language=he&format=json", city)
                    .retrieve()
                    .body(Map.class);

            if (response == null || !response.containsKey("results")) return null;

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results.isEmpty()) return null;

            Map<String, Object> first = results.get(0);
            double lat = ((Number) first.get("latitude")).doubleValue();
            double lon = ((Number) first.get("longitude")).doubleValue();
            return new double[]{lat, lon};
        } catch (Exception e) {
            return null;
        }
    }

    private String weatherCodeToHebrew(int code) {
        if (code == 0) return "שמיים בהירים";
        if (code <= 3) return "מעונן חלקית";
        if (code <= 48) return "ערפילי";
        if (code <= 67) return "גשם";
        if (code <= 77) return "שלג";
        if (code <= 82) return "ממטרים";
        return "סופת רעמים";
    }
}