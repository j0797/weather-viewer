package com.example.weatherviewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/locations")
public class LocationController {

    @GetMapping("/search")
    public String searchLocations() {
        return "search-results";
    }
}