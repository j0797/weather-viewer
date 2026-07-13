package com.example.weatherviewer.mapper;

import com.example.weatherviewer.dto.api.LocationDto;
import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "latitude", expression = "java(java.math.BigDecimal.valueOf(dto.lat()))")
    @Mapping(target = "longitude", expression = "java(java.math.BigDecimal.valueOf(dto.lon()))")
    Location toLocation(LocationDto dto, User user);
}