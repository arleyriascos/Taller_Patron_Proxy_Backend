package com.arley.arlynx;

import org.springframework.stereotype.Service;

@Service
public class PlanUpgradeService {

    private final UserAccountRepository accounts;

    public PlanUpgradeService(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    public SubscriptionPlan upgrade(String userId) {
        UserAccount account = accounts.findOrCreate(userId);
        account.upgradePlan();
        return account.plan();
    }
}
