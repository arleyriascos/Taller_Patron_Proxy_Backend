package com.arley.arlynx;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> rateLimited(RateLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(exception.retryAfterSeconds()))
                .body(new ErrorResponse("RATE_LIMIT_EXCEEDED", exception.getMessage(), exception.retryAfterSeconds()));
    }

    @ExceptionHandler(QuotaExhaustedException.class)
    public ResponseEntity<ErrorResponse> quotaExhausted(QuotaExhaustedException exception) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(ErrorResponse.of("QUOTA_EXHAUSTED", exception.getMessage()));
    }

    @ExceptionHandler(InvalidPlanChangeException.class)
    public ResponseEntity<ErrorResponse> invalidPlanChange(InvalidPlanChangeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("PLAN_CHANGE_NOT_ALLOWED", exception.getMessage()));
    }

    @ExceptionHandler({InvalidPromptException.class, InvalidUserIdException.class})
    public ResponseEntity<ErrorResponse> invalidInput(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("INVALID_REQUEST", exception.getMessage()));
    }

    @ExceptionHandler({ServletRequestBindingException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> malformedRequest(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("INVALID_REQUEST", "The request is malformed or misses a required header"));
    }

    @ExceptionHandler(GenerationInterruptedException.class)
    public ResponseEntity<ErrorResponse> generationInterrupted(GenerationInterruptedException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of("GENERATION_INTERRUPTED", exception.getMessage()));
    }
}
