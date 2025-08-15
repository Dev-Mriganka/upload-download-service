package com.vaultsearch.uploaddownloadservice.exception;

import lombok.Data;

import java.time.Instant;

@Data
public class ApiError {
    private String message;
    private String details;
    private Instant timestamp = Instant.now();

    public ApiError(String unexpectedErrorOccurred, String message) {
        this.message = unexpectedErrorOccurred;
        this.details = message;
    }
}