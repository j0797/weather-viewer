package com.example.weatherviewer.advice;

import com.example.weatherviewer.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OpenWeatherApiException.class)
    public String handleOpenWeatherApiException(OpenWeatherApiException e, Model model) {
        log.error("OpenWeather API error: {}", e.getMessage(), e);
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        return "error";
    }
}