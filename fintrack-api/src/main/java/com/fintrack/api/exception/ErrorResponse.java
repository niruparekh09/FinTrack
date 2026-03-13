package com.fintrack.api.exception;

import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timeStamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @PrePersist
    public void onError() {
        timeStamp = LocalDateTime.now();
    }
}