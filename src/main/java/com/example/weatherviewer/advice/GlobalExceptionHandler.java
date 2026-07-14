package com.example.weatherviewer.advice;

import com.example.weatherviewer.exception.auth.UserNotFoundException;
import com.example.weatherviewer.exception.location.LocationNotFoundException;
import com.example.weatherviewer.exception.openweather.OpenWeatherApiException;
import com.example.weatherviewer.exception.session.SessionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OpenWeatherApiException.class)
    public ModelAndView handleOpenWeatherApiException(OpenWeatherApiException e) {
        log.error("OpenWeather API error: {}", e.getMessage(), e);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", e.getMessage());
        mav.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        return mav;
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ModelAndView handleLocationNotFound(LocationNotFoundException e) {
        log.warn("Location not found: {}", e.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", e.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException e) {
        log.warn("User not found: {}", e.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", e.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ModelAndView handleSessionNotFound(SessionNotFoundException e) {
        log.warn("Session not found: {}", e.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "Your session has expired. Please log in again.");
        mav.setStatus(HttpStatus.UNAUTHORIZED);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "An unexpected error occurred. Please try again later.");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}