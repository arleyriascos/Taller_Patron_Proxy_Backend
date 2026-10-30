package com.arley.arlynx;

public enum SubscriptionPlan {
    FREE(10, 50_000L),
    PRO(60, 500_000L),
    ENTERPRISE(-1, -1L);

    private static final long UNLIMITED = -1L;

    private final int requestsPerMinute;
    private final long monthlyTokens;

    SubscriptionPlan(int requestsPerMinute, long monthlyTokens) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
    }

    public int requestsPerMinute() {
        return requestsPerMinute;
    }

    public long monthlyTokens() {
        return monthlyTokens;
    }

    public boolean hasUnlimitedRequests() {
        return requestsPerMinute == UNLIMITED;
    }

    public boolean hasUnlimitedTokens() {
        return monthlyTokens == UNLIMITED;
    }

    public boolean hasNext() {
        return ordinal() < values().length - 1;
    }

    public SubscriptionPlan next() {
        return values()[ordinal() + 1];
    }
}
