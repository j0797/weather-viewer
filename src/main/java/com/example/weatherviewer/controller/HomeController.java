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

    private final LocationService locationService;

    public HomeController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String home(@RequestAttribute(value = "currentUser") User user,
                       Model model) {
        if (user != null) {
            List<WeatherCardDto> weatherCards = locationService.getWeatherForUser(user);
            model.addAttribute("weatherCards", weatherCards);
            model.addAttribute("currentUser", user);
        }
        return "index";
    }
}