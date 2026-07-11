package com.example.weatherviewer.service;

import com.example.weatherviewer.config.TestDatabaseConfig;
import com.example.weatherviewer.dto.auth.SignUpDto;
import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
@Transactional
@Rollback
public class SessionServiceTest {

    private static final int EXPIRED_SESSION_HOURS = 1;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    void createSession_shouldPersistSessionInDatabase() {
        SignUpDto signUpDto = new SignUpDto("testuser", "Password123", "Password123");
        User savedUser = userService.register(signUpDto);

        Session session = sessionService.createSession(savedUser);

        assertNotNull(session.getId());
        assertNotNull(session.getExpiresAt());
        assertTrue(session.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void session_shouldBeValid_whenActive() {
        SignUpDto signUpDto = new SignUpDto("testuser", "Password123", "Password123");
        User savedUser = userService.register(signUpDto);

        Session session = sessionService.createSession(savedUser);
        boolean valid = sessionService.isValid(session.getId());

        assertTrue(valid);
    }

    @Test
    void session_shouldBeInvalid_whenExpired() {
        SignUpDto signUpDto = new SignUpDto("testuser", "Password123", "Password123");
        User savedUser = userService.register(signUpDto);
        Session expiredSession = new Session(savedUser, Instant.now().minus(EXPIRED_SESSION_HOURS, ChronoUnit.HOURS));

        Session savedSession = sessionRepository.save(expiredSession);
        boolean valid = sessionService.isValid(savedSession.getId());

        assertFalse(valid);
    }
}