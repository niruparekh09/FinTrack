package com.fintrack.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    private final long jwtExpiration = 1000 * 60 * 60 * 24; // 24h

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails, Long id) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", id)
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpiration))
                .signWith(getSignKey())
                .compact();
        log.info("Generated JWT token for user: {} with id: {}", userDetails.getUsername(), id);
        return token;
    }

    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            log.debug("Extracted username '{}' from token", username);
            return username;
        } catch (Exception e) {
            log.error("Failed to extract username from token", e);
            throw e;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", token, e);
            throw e;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String userName = extractUsername(token);
            boolean isValid = userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
            log.debug("Token validation for user '{}': {}", userDetails.getUsername(), isValid);
            return isValid;
        } catch (Exception e) {
            log.warn("Token validation failed for user '{}'", userDetails.getUsername(), e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        log.debug("Token expired: {}", expired);
        return expired;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}