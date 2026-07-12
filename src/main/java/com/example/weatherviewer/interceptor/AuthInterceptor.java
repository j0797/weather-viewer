package com.example.weatherviewer.interceptor;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.service.SessionService;
import com.example.weatherviewer.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    private final SessionService sessionService;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Cookie[] cookies = request.getCookies();
        log.debug("Intercepting request: {}", request.getRequestURI());

        if (cookies == null) {
            log.debug("No cookies, redirecting to login");
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
                        log.debug("User loaded: {}", user.getLogin());
                    } else {
                        log.debug("Session expired: {}", sessionId);
                        invalidateCookie(response);
                    }
                } catch (Exception e) {
                    log.debug("Invalid session cookie: {}", cookie.getValue());
                    invalidateCookie(response);
                    response.sendRedirect("/auth/sign-in");
                    return false;
                }
                break;
            }
        }
        if (request.getAttribute(CURRENT_USER) == null) {
            log.debug("No user in request, redirecting to login");
            response.sendRedirect("/auth/sign-in");
            return false;
        }
        log.debug("Request allowed for user: {}", ((User) request.getAttribute(CURRENT_USER)).getLogin());
        return true;
    }

    private void invalidateCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_ID, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}