package com.example.weatherviewer.repository.impl;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.repository.SessionRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final SessionFactory sessionFactory;

    public SessionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Session> findById(UUID id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Session.class, id));
    }

    @Override
    public void deleteById(UUID id) {
        Session session = sessionFactory.getCurrentSession().get(Session.class, id);
        if (session != null) {
            sessionFactory.getCurrentSession().remove(session);
        }
    }

    @Override
    public Session save(Session session) {
        sessionFactory.getCurrentSession().persist(session);
        return session;
    }

    @Override
    public int deleteExpiredSessions(Instant now) {
        return sessionFactory.getCurrentSession()
                .createMutationQuery("DELETE FROM Session WHERE expiresAt < :now")
                .setParameter("now", now)
                .executeUpdate();
    }
}