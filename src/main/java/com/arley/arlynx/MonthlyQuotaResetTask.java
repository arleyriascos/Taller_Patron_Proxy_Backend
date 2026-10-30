package com.arley.arlynx;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonthlyQuotaResetTask {

    private final UserAccountRepository accounts;

    public MonthlyQuotaResetTask(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "UTC")
    public void resetQuotas() {
        accounts.resetAllUsage();
    }
}
