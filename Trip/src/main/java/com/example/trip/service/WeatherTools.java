package com.example.trip.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class WeatherTools {

    private final WeatherService weatherService;

    public WeatherTools(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Tool(description = """
        קבלת תחזית מזג אוויר ל-3 הימים הקרובים עבור עיר או אזור.
        השתמש בכלי הזה כאשר המשתמש שואל על מזג האוויר,
        או כאשר אתה זקוק למזג האוויר כדי להמליץ על טיול (למשל האם צפוי גשם).
        """)
    public String getWeatherByCity(String city) {
        return weatherService.getWeatherForCity(city);
    }
}