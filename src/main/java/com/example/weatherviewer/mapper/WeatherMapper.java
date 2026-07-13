package com.example.weatherviewer.mapper;

import com.example.weatherviewer.dto.api.WeatherDto;
import com.example.weatherviewer.dto.view.WeatherCardDto;
import com.example.weatherviewer.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WeatherMapper {
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "name", source = "location.name")
    @Mapping(target = "country", source = "weather.sys.country")
    @Mapping(target = "temperature", source = "weather.main.temp")
    @Mapping(target = "feelsLike", source = "weather.main.feels_like")
    @Mapping(target = "humidity", source = "weather.main.humidity")
    @Mapping(target = "weatherDescription", expression = "java(weather.weather().getFirst().description())")
    @Mapping(target = "icon", expression = "java(weather.weather().getFirst().icon())")
    WeatherCardDto toWeatherCard(Location location, WeatherDto weather);
}