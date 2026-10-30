package com.arley.arlynx;

public record GenerationResponse(String text, int promptTokens, int completionTokens) {
}
