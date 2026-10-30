package com.arley.arlynx;

public class UserAccount {

    private final String userId;
    private SubscriptionPlan plan = SubscriptionPlan.FREE;
    private long tokensUsed;

    public UserAccount(String userId) {
        this.userId = userId;
    }

    public String userId() {
        return userId;
    }

    public synchronized SubscriptionPlan plan() {
        return plan;
    }

    public synchronized long tokensUsed() {
        return tokensUsed;
    }

    public synchronized long tokensRemaining() {
        if (plan.hasUnlimitedTokens()) {
            return -1L;
        }
        return Math.max(0L, plan.monthlyTokens() - tokensUsed);
    }

    public synchronized boolean canAfford(long tokens) {
        return plan.hasUnlimitedTokens() || plan.monthlyTokens() - tokensUsed >= tokens;
    }

    public synchronized long consume(long tokens) {
        long before = tokensUsed;
        long updated = tokensUsed + tokens;
        tokensUsed = plan.hasUnlimitedTokens() ? updated : Math.min(updated, plan.monthlyTokens());
        return tokensUsed - before;
    }

    public synchronized void upgradePlan() {
        if (!plan.hasNext()) {
            throw new InvalidPlanChangeException(plan);
        }
        plan = plan.next();
    }

    public synchronized void resetUsage() {
        tokensUsed = 0L;
    }
}
