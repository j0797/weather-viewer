package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.LocationDto;
import com.example.weatherviewer.dto.WeatherDto;
import com.example.weatherviewer.exception.OpenWeatherApiException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class OpenWeatherServiceTest {

    private static final int WIREMOCK_PORT = 8089;
    private static final String API_URL = "http://localhost:" + WIREMOCK_PORT + "/";
    private static final String API_KEY = "test-key";
    private WireMockServer wireMockServer;
    private OpenWeatherService openWeatherService;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        openWeatherService = new OpenWeatherService(new RestTemplate(), API_URL, API_KEY);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void searchLocations_shouldReturnLocations() {
        wireMockServer.stubFor(get(urlPathMatching("/geo/1.0/direct.*")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[{\"name\":\"London\",\"country\":\"GB\",\"lat\":51.5,\"lon\":-0.1}]")));
        List<LocationDto> result = openWeatherService.searchLocations("London");
        assertEquals(1, result.size());
        assertEquals("London", result.getFirst().name());
    }

    @Test
    void searchLocations_shouldThrowException_onHttpError() {
        wireMockServer.stubFor(get(urlPathMatching("/geo/1.0/direct.*")).willReturn(aResponse().withStatus(500)));
        assertThrows(OpenWeatherApiException.class, () -> openWeatherService.searchLocations("London"));
    }

    @Test
    void getWeather_shouldReturnWeatherDto() {
        wireMockServer.stubFor(get(urlPathMatching("/data/2.5/weather.*")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\"name\":\"London\",\"main\":{\"temp\":20.0,\"feels_like\":18.0,\"humidity\":65},\"weather\":[{\"description\":\"clear sky\",\"icon\":\"01d\"}],\"sys\":{\"country\":\"GB\"}}")));
        WeatherDto result = openWeatherService.getWeather(51.5, -0.1);
        assertNotNull(result);
        assertEquals("London", result.name());
        assertEquals(20.0, result.main().temp());
    }

    @Test
    void getWeather_shouldThrowException_onHttpError() {
        wireMockServer.stubFor(get(urlPathMatching("/data/2.5/weather.*")).willReturn(aResponse().withStatus(500)));
        assertThrows(OpenWeatherApiException.class, () -> openWeatherService.getWeather(51.5, -0.1));
    }
}