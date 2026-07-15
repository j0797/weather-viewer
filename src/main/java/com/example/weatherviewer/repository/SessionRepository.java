package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.Session;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    Optional<Session> findById(UUID id);

    void deleteById(UUID id);

    Session save(Session session);

    int deleteExpiredSessions(Instant now);
}