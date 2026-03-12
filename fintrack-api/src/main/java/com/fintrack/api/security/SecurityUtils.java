package com.fintrack.api.security;

import com.fintrack.api.exception.UserNotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUsername() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) { // avoids NPE and avoids unsafe cast
            return userDetails.getEmail(); // or getUsername()
        }

        throw new UserNotAuthenticatedException("Invalid authentication principal");
    }

    public static Long getCurrentUserId() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        throw new UserNotAuthenticatedException("Invalid authentication principal");
    }
}