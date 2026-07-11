package com.example.weatherviewer.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationDto(
        String name,
        String country,
        String state,
        double lat,
        double lon) {
}