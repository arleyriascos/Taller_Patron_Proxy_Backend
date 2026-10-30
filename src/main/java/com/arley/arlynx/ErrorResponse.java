package com.arley.arlynx;

public record ErrorResponse(String code, String message, Long retryAfterSeconds) {

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null);
    }
}
