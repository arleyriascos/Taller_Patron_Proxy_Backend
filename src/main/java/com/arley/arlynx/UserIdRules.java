package com.arley.arlynx;

public final class UserIdRules {

    public static final int MAX_LENGTH = 64;

    private UserIdRules() {
    }

    public static String require(String rawUserId) {
        if (rawUserId == null || rawUserId.isBlank()) {
            throw new InvalidUserIdException("The X-User-Id header is required");
        }
        String userId = rawUserId.strip();
        if (userId.length() > MAX_LENGTH) {
            throw new InvalidUserIdException("The X-User-Id header must have at most " + MAX_LENGTH + " characters");
        }
        return userId;
    }
}
