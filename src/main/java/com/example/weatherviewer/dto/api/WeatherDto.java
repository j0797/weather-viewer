package com.example.weatherviewer.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDto(
        Main main,
        List<Weather> weather,
        String name,
        Sys sys
) {
    public record Main(
            double temp,
            double feels_like,
            int humidity
    ) {
    }

    public record Weather(
            String description,
            String icon
    ) {
    }

    public record Sys(
            String country
    ) {
    }
}