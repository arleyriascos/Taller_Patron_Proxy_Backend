package com.arley.arlynx;

public record GenerationRequest(String userId, String prompt) {

    public static final int MAX_PROMPT_LENGTH = 8000;

    public GenerationRequest {
        userId = UserIdRules.require(userId);
        if (prompt == null || prompt.isBlank()) {
            throw new InvalidPromptException("The prompt must not be blank");
        }
        prompt = prompt.strip();
        if (prompt.length() > MAX_PROMPT_LENGTH) {
            throw new InvalidPromptException("The prompt must have at most " + MAX_PROMPT_LENGTH + " characters");
        }
    }
}
