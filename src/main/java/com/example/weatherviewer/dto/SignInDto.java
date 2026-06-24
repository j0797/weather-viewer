package com.example.weatherviewer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignInDto(
        @NotBlank(message = "Login is required")
        @Size(min = 5, max = 50, message = "Login must be between 5 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Login may contain only English letters, digits and underscores")
        String login,

        @NotBlank(message = "Password is required")
        String password) {
}