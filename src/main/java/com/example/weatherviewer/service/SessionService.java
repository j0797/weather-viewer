package com.example.weatherviewer.service;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.SessionNotFoundException;
import com.example.weatherviewer.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class SessionService {

    private static final long SESSION_TTL_HOURS = 24;
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }


    public Session createSession(User user) {
        Instant expiresAt = Instant.now().plus(SESSION_TTL_HOURS, ChronoUnit.HOURS);
        Session session = new Session(user, expiresAt);
        return sessionRepository.save(session);

    }

    @Transactional(readOnly = true)
    public Session findById(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }

    public void deleteSession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    @Transactional(readOnly = true)
    public boolean isValid(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .map(session -> session.getExpiresAt().isAfter(Instant.now()))
                .orElse(false);
    }
}