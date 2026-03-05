package com.fintrack.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method is triggered whenever an unauthenticated user
     * tries to access a protected resource.
     *
     * Example cases:
     * - No JWT token provided
     * - Invalid JWT token
     * - Expired JWT token
     *
     * Instead of letting Spring return a default HTML error page,
     * we return a clean JSON response that frontend clients can handle.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        // Set HTTP status code to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Ensure response is returned as JSON instead of default HTML
        response.setContentType("application/json");

        // Send a structured JSON error response to the client
        // This keeps API responses consistent for frontend/mobile clients
        response.getWriter().write("""
                {
                  "status":401,
                  "message":"Unauthorized: Invalid or expired token"
                }
                """);
    }
}