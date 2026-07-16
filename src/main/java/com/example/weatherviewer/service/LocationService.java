package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.api.LocationDto;
import com.example.weatherviewer.dto.api.WeatherDto;
import com.example.weatherviewer.dto.view.WeatherCardDto;
import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.location.LocationAlreadyExistsException;
import com.example.weatherviewer.exception.location.LocationNotFoundException;
import com.example.weatherviewer.exception.openweather.OpenWeatherApiException;
import com.example.weatherviewer.mapper.LocationMapper;
import com.example.weatherviewer.mapper.WeatherMapper;
import com.example.weatherviewer.repository.LocationRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepository locationRepository;
    private final OpenWeatherService openWeatherService;
    private final LocationMapper locationMapper;
    private final WeatherMapper weatherMapper;

    public LocationService(LocationRepository locationRepository,
                           OpenWeatherService openWeatherService,
                           LocationMapper locationMapper,
                           WeatherMapper weatherMapper) {
        this.locationRepository = locationRepository;
        this.openWeatherService = openWeatherService;
        this.locationMapper = locationMapper;
        this.weatherMapper = weatherMapper;
    }

    public Location addLocation(User user, String name, double lat, double lon, String country, String state) {
        log.info("Adding location '{}' for user: {}", name, user.getLogin());

        String message = "Location '" + name + "' in " + country + (state != null ? ", " + state : "") + " already exists";
        if (locationRepository.findByUserIdAndNameAndCountryAndState(user.getId(), name, country, state).isPresent()) {
            log.warn("Duplicate location '{}' for user: {}", name, user.getLogin());
            throw new LocationAlreadyExistsException(message);
        }

        LocationDto dto = new LocationDto(name, country, state, lat, lon);
        Location location = locationMapper.toLocation(dto, user);

        try {
            Location saved = locationRepository.save(location);
            log.info("Location added successfully: {}", name);
            return saved;
        } catch (ConstraintViolationException e) {
            throw new LocationAlreadyExistsException(message);
        }
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
                    return weatherMapper.toWeatherCard(location, weather);
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