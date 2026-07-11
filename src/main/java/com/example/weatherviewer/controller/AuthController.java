package com.example.weatherviewer.controller;

import com.example.weatherviewer.dto.auth.SignInDto;
import com.example.weatherviewer.dto.auth.SignUpDto;
import com.example.weatherviewer.exception.auth.InvalidCredentialsException;
import com.example.weatherviewer.exception.auth.UserAlreadyExistsException;
import com.example.weatherviewer.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final String PAGE_SIGN_UP = "sign-up";
    private static final String PAGE_SIGN_IN = "sign-in";
    private static final String ERROR = "error";
    private static final String REDIRECT = "redirect:/";
    private static final String SESSION_ID = "sessionId";
    private static final String MODEL_ATTRIBUTE_SIGN_UP_DTO = "signUpDto";
    private static final String MODEL_ATTRIBUTE_SIGN_IN_DTO = "signInDto";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid login or password";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User with this login already exists";
    private static final String COOKIE_PATH = "/";
    private static final int COOKIE_MAX_AGE_DELETE = 0;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_SIGN_UP_DTO, new SignUpDto("", "", ""));
        return PAGE_SIGN_UP;
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute(MODEL_ATTRIBUTE_SIGN_UP_DTO) SignUpDto signUpDto,
                         BindingResult bindingResult,
                         HttpServletResponse response,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return PAGE_SIGN_UP;
        }

        try {
            UUID sessionId = authService.register(signUpDto);
            setSessionCookie(response, sessionId);
            return REDIRECT;
        } catch (UserAlreadyExistsException e) {
            model.addAttribute(ERROR, USER_ALREADY_EXISTS_MESSAGE);
            return PAGE_SIGN_UP;
        } catch (IllegalArgumentException e) {
            model.addAttribute(ERROR, e.getMessage());
            return PAGE_SIGN_UP;
        }
    }

    @GetMapping("/sign-in")
    public String showSignInForm(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_SIGN_IN_DTO, new SignInDto("", ""));
        return PAGE_SIGN_IN;
    }

    @PostMapping("/sign-in")
    public String signIn(@Valid @ModelAttribute(MODEL_ATTRIBUTE_SIGN_IN_DTO) SignInDto signInDto,
                         BindingResult bindingResult,
                         HttpServletResponse response,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return PAGE_SIGN_IN;
        }

        try {
            UUID sessionId = authService.login(signInDto);
            setSessionCookie(response, sessionId);
            return REDIRECT;
        } catch (InvalidCredentialsException e) {
            model.addAttribute(ERROR, INVALID_CREDENTIALS_MESSAGE);
            return PAGE_SIGN_IN;
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_ID.equals(cookie.getName())) {
                    try {
                        authService.logout(UUID.fromString(cookie.getValue()));
                    } catch (Exception ignored) {
                    }
                    break;
                }
            }
        }
        Cookie cookie = new Cookie(SESSION_ID, null);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(COOKIE_MAX_AGE_DELETE);
        response.addCookie(cookie);
        return REDIRECT;
    }

    private void setSessionCookie(HttpServletResponse response, UUID sessionId) {
        Cookie cookie = new Cookie(SESSION_ID, sessionId.toString());
        cookie.setHttpOnly(true);
        cookie.setPath(COOKIE_PATH);
        response.addCookie(cookie);
    }
}