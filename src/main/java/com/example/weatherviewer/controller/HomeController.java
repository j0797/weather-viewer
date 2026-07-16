package com.example.weatherviewer.controller;

import com.example.weatherviewer.dto.view.WeatherCardDto;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.util.List;

@Controller
public class HomeController {

    private static final String PAGE_INDEX = "index";
    private static final String MODEL_ATTRIBUTE_WEATHER_CARDS = "weatherCards";
    private static final String MODEL_ATTRIBUTE_CURRENT_USER = "currentUser";

    private final LocationService locationService;

    public HomeController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String home(@RequestAttribute(value = MODEL_ATTRIBUTE_CURRENT_USER) User user,
                       Model model) {
        if (user != null) {
            List<WeatherCardDto> weatherCards = locationService.getWeatherForUser(user);
            model.addAttribute(MODEL_ATTRIBUTE_WEATHER_CARDS, weatherCards);
            model.addAttribute(MODEL_ATTRIBUTE_CURRENT_USER, user);
        }
        return PAGE_INDEX;
    }
}