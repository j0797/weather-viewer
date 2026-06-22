package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.Location;

import java.util.List;

public interface LocationRepository {
    List<Location> findByUserId(Long userId);

    void deleteById(Long id);

    Location save(Location location);
}