package com.fintrack.api.user;

public record UserResponse(
        Long id,
        String name,
        String email
) {
}
