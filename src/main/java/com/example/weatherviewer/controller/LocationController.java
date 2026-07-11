package com.example.weatherviewer.controller;

import com.example.weatherviewer.dto.api.LocationDto;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.location.LocationAlreadyExistsException;
import com.example.weatherviewer.exception.location.LocationNotFoundException;
import com.example.weatherviewer.service.LocationService;
import com.example.weatherviewer.service.OpenWeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private static final String SEARCH_RESULTS_VIEW = "search-results";
    private static final String REDIRECT_HOME = "redirect:/";
    private static final String QUERY_PARAM = "query";
    private static final String EXISTING_KEYS = "existingKeys";
    private static final String LOCATIONS = "locations";
    private static final String ERROR = "error";
    private static final String SUCCESS = "success";

    private final OpenWeatherService openWeatherService;
    private final LocationService locationService;

    public LocationController(OpenWeatherService openWeatherService, LocationService locationService) {
        this.openWeatherService = openWeatherService;
        this.locationService = locationService;
    }

    @GetMapping("/search")
    public String searchLocations(@RequestParam("query") String query,
                                  @RequestAttribute("currentUser") User user,
                                  Model model) {
        List<LocationDto> locations = openWeatherService.searchLocations(query);
        Set<String> existingKeys = locationService.getLocationsByUser(user)
                .stream()
                .map(l -> l.getName().toLowerCase() + "|" +
                        (l.getCountry() != null ? l.getCountry() : "") + "|" +
                        (l.getState() != null ? l.getState() : ""))
                .collect(Collectors.toSet());
        model.addAttribute(LOCATIONS, locations);
        model.addAttribute(QUERY_PARAM, query);
        model.addAttribute(EXISTING_KEYS, existingKeys);
        return SEARCH_RESULTS_VIEW;
    }

    @PostMapping("/add")
    public String addLocation(@RequestParam("name") String name,
                              @RequestParam("lat") double lat,
                              @RequestParam("lon") double lon,
                              @RequestParam("country") String country,
                              @RequestParam(value = "state", required = false) String state,
                              @RequestAttribute("currentUser") User user,
                              RedirectAttributes redirectAttributes) {
        try {
            locationService.addLocation(user, name, lat, lon, country, state);
            redirectAttributes.addFlashAttribute(SUCCESS, "Location added successfully");
        } catch (LocationAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR, "Failed to add location");
        }
        return REDIRECT_HOME;
    }

    @PostMapping("/delete/{id}")
    public String deleteLocation(@PathVariable Long id,
                                 @RequestAttribute("currentUser") User user,
                                 RedirectAttributes redirectAttributes) {
        try {
            locationService.deleteLocation(id, user);
            redirectAttributes.addFlashAttribute(SUCCESS, "Location deleted successfully");
        } catch (LocationNotFoundException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR, "Failed to delete location");
        }
        return REDIRECT_HOME;
    }
}