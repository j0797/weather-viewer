package com.example.weatherviewer.exception.openweather;

public class OpenWeatherApiException extends RuntimeException {
    public OpenWeatherApiException(String message) {
        super(message);
    }
}