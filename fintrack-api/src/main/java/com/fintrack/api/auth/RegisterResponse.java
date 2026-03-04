package com.fintrack.api.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
