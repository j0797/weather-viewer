package com.example.weatherviewer.exception;

public class OpenWeatherApiException extends RuntimeException {
    public OpenWeatherApiException(String message) {
        super(message);
    }
}