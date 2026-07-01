package com.example.weatherviewer.dto;

public record LocationDto(
        String name,
        String country,
        String state,
        double lat,
        double lon,
        boolean alreadyAdded) {
}