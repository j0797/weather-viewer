package com.example.weatherviewer.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.weatherviewer.dto.auth.SignInDto;
import com.example.weatherviewer.dto.auth.SignUpDto;
import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.auth.InvalidCredentialsException;
import com.example.weatherviewer.exception.auth.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;
    private final SessionService sessionService;

    public AuthService(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public UUID register(SignUpDto signUpDto) {
        log.info("Registration attempt for user: {}", signUpDto.login());
        User user = userService.register(signUpDto);
        Session session = sessionService.createSession(user);
        log.info("User registered successfully: {}", user.getLogin());
        return session.getId();
    }

    public UUID login(SignInDto signInDto) {
        String login = signInDto.login();
        String rawPassword = signInDto.password();
        log.info("Login attempt for user: {}", signInDto.login());
        User user;
        try {
            user = userService.findByLogin(login);
        } catch (UserNotFoundException e) {
            log.warn("Failed login attempt: user not found - {}", login);
            throw new InvalidCredentialsException("Invalid login or password");
        }
        String storedHash = user.getPassword();
        boolean passwordMatches = BCrypt.verifyer()
                .verify(rawPassword.toCharArray(), storedHash)
                .verified;

        if (!passwordMatches) {
            log.warn("Failed login attempt: wrong password for user - {}", login);
            throw new InvalidCredentialsException("Invalid login or password");
        }

        Session session = sessionService.createSession(user);
        log.info("User logged in successfully: {}", login);
        return session.getId();
    }

    public void logout(UUID sessionId) {
        log.info("Logout for session: {}", sessionId);
        sessionService.deleteSession(sessionId);
    }
}