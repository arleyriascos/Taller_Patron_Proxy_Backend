package com.arley.arlynx;

import org.springframework.stereotype.Service;

@Service
public class QuotaStatusProvider {

    private final UserAccountRepository accounts;
    private final RequestWindowTracker tracker;
    private final MinuteWindow window;
    private final BillingCycle billingCycle;

    public QuotaStatusProvider(
            UserAccountRepository accounts,
            RequestWindowTracker tracker,
            MinuteWindow window,
            BillingCycle billingCycle) {
        this.accounts = accounts;
        this.tracker = tracker;
        this.window = window;
        this.billingCycle = billingCycle;
    }

    public QuotaStatusResponse statusFor(String userId) {
        UserAccount account = accounts.findOrCreate(userId);
        SubscriptionPlan plan = account.plan();
        return new QuotaStatusResponse(
                plan,
                account.tokensUsed(),
                account.tokensRemaining(),
                plan.monthlyTokens(),
                billingCycle.nextResetDate(),
                tracker.used(userId),
                plan.requestsPerMinute(),
                window.secondsUntilReset());
    }
}
