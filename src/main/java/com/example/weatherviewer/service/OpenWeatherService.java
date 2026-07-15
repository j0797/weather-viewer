package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.api.LocationDto;
import com.example.weatherviewer.dto.api.WeatherDto;
import com.example.weatherviewer.exception.openweather.OpenWeatherApiException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Service
public class OpenWeatherService {
    private static final Logger log = LoggerFactory.getLogger(OpenWeatherService.class);
    private static final String GEOCODING_URL = "geo/1.0/direct?q={query}&limit={limit}&appid={apiKey}";
    private static final String WEATHER_URL = "data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}&units=metric";
    private static final int SEARCH_LIMIT = 5;
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final Cache<String, WeatherDto> weatherCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .maximumSize(100)
            .build();

    public OpenWeatherService(RestTemplate restTemplate,
                              @Value("${weather.api.url}") String apiUrl,
                              @Value("${weather.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public List<LocationDto> searchLocations(String query) {
        log.info("Searching locations for query: {}", query);
        try {
            String url = apiUrl + GEOCODING_URL;
            LocationDto[] response = restTemplate.getForObject(url, LocationDto[].class, query, SEARCH_LIMIT, apiKey);
            if (response != null) {
                log.info("Found {} locations for query: {}", response.length, query);
                return List.of(response);
            } else {
                log.info("No locations found for query: {}", query);
                return List.of();
            }
        } catch (RestClientException e) {
            log.error("Error searching locations for query: {}", query, e);
            throw new OpenWeatherApiException("Location search is temporarily unavailable. Please try again later.");
        }
    }

    public WeatherDto getWeather(double lat, double lon) {
        String key = lat + "," + lon;
        return weatherCache.get(key, k -> fetchWeather(lat, lon));
    }

    private WeatherDto fetchWeather(double lat, double lon) {
        log.info("Fetching weather for lat={}, lon={}", lat, lon);
        try {
            String url = apiUrl + WEATHER_URL;
            WeatherDto dto = restTemplate.getForObject(url, WeatherDto.class, lat, lon, apiKey);
            if (dto == null) {
                throw new OpenWeatherApiException("Weather data is temporarily unavailable. Please try again later.");
            }
            log.info("Weather fetched for lat={}, lon={}", lat, lon);
            return dto;
        } catch (RestClientException e) {
            log.error("Error fetching weather for lat={}, lon={}", lat, lon, e);
            throw new OpenWeatherApiException("Weather service is temporarily unavailable. Please try again later.");
        }
    }
}