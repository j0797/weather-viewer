package com.example.weatherviewer.interceptor;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private static final String SESSION_ID = "sessionId";
    private static final String CURRENT_USER = "currentUser";
    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.sendRedirect("/auth/sign-in");
            return false;
        }
        for (Cookie cookie : cookies) {
            if (SESSION_ID.equals(cookie.getName())) {
                try {
                    UUID sessionId = UUID.fromString(cookie.getValue());
                    Session session = sessionService.findById(sessionId);
                    if (session.getExpiresAt().isAfter(Instant.now())) {
                        request.setAttribute(CURRENT_USER, session.getUser());
                    }
                } catch (Exception ignored) {
                }
                break;
            }
        }
        if (request.getAttribute(CURRENT_USER) == null) {
            response.sendRedirect("/auth/sign-in");
            return false;
        }
        return true;
    }
}