package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.WeatherCardDto;
import com.example.weatherviewer.dto.WeatherDto;
import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.LocationAlreadyExistsException;
import com.example.weatherviewer.exception.LocationNotFoundException;
import com.example.weatherviewer.exception.OpenWeatherApiException;
import com.example.weatherviewer.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;
    private final OpenWeatherService openWeatherService;

    public LocationService(LocationRepository locationRepository, OpenWeatherService openWeatherService) {
        this.locationRepository = locationRepository;
        this.openWeatherService = openWeatherService;
    }

    public Location addLocation(User user, String name, double lat, double lon, String country, String state) {

        if (locationRepository.findByUserIdAndNameAndCountryAndState(user.getId(), name, country, state).isPresent()) {
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
        List<Location> locations = getLocationsByUser(user);
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

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found for this user"));
        if (!location.getUser().getId().equals(user.getId())) {
            throw new LocationNotFoundException("Location not found for this user");
        }
        locationRepository.deleteById(locationId);
    }
}