package com.example.weatherviewer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpDto(
        @NotBlank(message = "Login is required")
        @Size(min = 5, max = 50, message = "Login must be between 5 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Login may contain only English letters, digits and underscores")
        String login,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).+$", message = "Password must contain at least one digit, one lowercase and one uppercase letter")
        String password,

        @NotBlank(message = "Password confirmation is required")
        String passwordConfirmation) {
}