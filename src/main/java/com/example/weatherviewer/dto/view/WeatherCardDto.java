package com.example.weatherviewer.dto.view;

public record WeatherCardDto(
        Long locationId,
        String name,
        String country,
        double temperature,
        double feelsLike,
        int humidity,
        String weatherDescription,
        String icon) {
}