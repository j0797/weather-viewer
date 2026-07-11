package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {

    List<Location> findAllByUserId(Long userId);

    Optional<Location> findById(Long id);

    Optional<Location> findByUserIdAndNameAndCountryAndState(Long userId, String name, String country, String state);

    void deleteById(Long id);

    Location save(Location location);
}