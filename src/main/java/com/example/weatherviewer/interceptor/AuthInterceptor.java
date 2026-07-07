package com.example.weatherviewer.interceptor;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.service.SessionService;
import com.example.weatherviewer.service.UserService;
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
    private final UserService userService;

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
                        User user = userService.findById(session.getUser().getId());
                        request.setAttribute(CURRENT_USER, user);
                    } else {
                        invalidateCookie(response);
                    }
                } catch (Exception e) {
                    invalidateCookie(response);
                    response.sendRedirect("/auth/sign-in");
                    return false;
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

    private void invalidateCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_ID, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}