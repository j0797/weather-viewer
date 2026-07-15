package com.example.weatherviewer.scheduler;

import com.example.weatherviewer.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class SessionCleanupScheduler {
    private static final Logger log = LoggerFactory.getLogger(SessionCleanupScheduler.class);
    private final SessionRepository sessionRepository;

    public SessionCleanupScheduler(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Scheduled(cron = "0 0 */3 * * *")
    @Transactional
    public void deleteExpiredSessions() {
        int deleted = sessionRepository.deleteExpiredSessions(Instant.now());
        if (deleted > 0) {
            log.info("Deleted {} expired sessions", deleted);
        }
    }
}