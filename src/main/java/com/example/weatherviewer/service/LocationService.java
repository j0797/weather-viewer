package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.view.WeatherCardDto;
import com.example.weatherviewer.dto.api.WeatherDto;
import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.location.LocationAlreadyExistsException;
import com.example.weatherviewer.exception.location.LocationNotFoundException;
import com.example.weatherviewer.exception.openweather.OpenWeatherApiException;
import com.example.weatherviewer.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class LocationService {
    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepository locationRepository;
    private final OpenWeatherService openWeatherService;

    public LocationService(LocationRepository locationRepository, OpenWeatherService openWeatherService) {
        this.locationRepository = locationRepository;
        this.openWeatherService = openWeatherService;
    }

    public Location addLocation(User user, String name, double lat, double lon, String country, String state) {
        log.info("Adding location '{}' for user: {}", name, user.getLogin());
        if (locationRepository.findByUserIdAndNameAndCountryAndState(user.getId(), name, country, state).isPresent()) {
            log.warn("Duplicate location '{}' for user: {}", name, user.getLogin());
            throw new LocationAlreadyExistsException("Location '" + name + "' in " + country + (state != null ? ", " + state : "") + " already exists");
        }
        Location location = new Location(
                name,
                user,
                BigDecimal.valueOf(lat),
                BigDecimal.valueOf(lon),
                country,
                state);
        return locationRepository.save(location);
    }

    @Transactional(readOnly = true)
    public List<Location> getLocationsByUser(User user) {
        return locationRepository.findAllByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<WeatherCardDto> getWeatherForUser(User user) {
        log.info("Fetching weather for user: {}", user.getLogin());
        List<Location> locations = getLocationsByUser(user);
        log.info("Found {} locations for user: {}", locations.size(), user.getLogin());
        return locations.stream()
                .map(location -> {
                    WeatherDto weather = openWeatherService.getWeather(
                            location.getLatitude().doubleValue(),
                            location.getLongitude().doubleValue()
                    );
                    if (weather.weather() == null || weather.weather().isEmpty()) {
                        throw new OpenWeatherApiException("Weather data is temporarily unavailable for this location.");
                    }
                    return new WeatherCardDto(
                            location.getId(),
                            location.getName(),
                            weather.sys().country(),
                            weather.main().temp(),
                            weather.main().feels_like(),
                            weather.main().humidity(),
                            weather.weather().getFirst().description(),
                            weather.weather().getFirst().icon()
                    );
                })
                .toList();
    }

    public void deleteLocation(Long locationId, User user) {
        log.info("Deleting location {} for user: {}", locationId, user.getLogin());
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found for this user"));
        if (!location.getUser().getId().equals(user.getId())) {
            throw new LocationNotFoundException("Location not found for this user");
        }
        log.info("Location deleted successfully: {}", locationId);
        locationRepository.deleteById(locationId);
    }
}