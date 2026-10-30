package com.arley.arlynx;

public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService delegate;
    private final UserAccountRepository accounts;
    private final RequestWindowTracker tracker;
    private final MinuteWindow window;

    public RateLimitProxyService(
            AIGenerationService delegate,
            UserAccountRepository accounts,
            RequestWindowTracker tracker,
            MinuteWindow window) {
        this.delegate = delegate;
        this.accounts = accounts;
        this.tracker = tracker;
        this.window = window;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        SubscriptionPlan plan = accounts.findOrCreate(request.userId()).plan();
        if (!plan.hasUnlimitedRequests() && !tracker.tryAcquire(request.userId(), plan.requestsPerMinute())) {
            throw new RateLimitExceededException(window.secondsUntilReset());
        }
        return delegate.generate(request);
    }
}
