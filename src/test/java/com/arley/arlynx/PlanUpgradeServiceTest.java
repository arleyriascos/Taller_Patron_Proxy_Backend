package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PlanUpgradeServiceTest {

    @Test
    void movesFromFreeToProToEnterpriseAndThenRefuses() {
        PlanUpgradeService service = new PlanUpgradeService(new InMemoryUserAccountRepository());
        assertEquals(SubscriptionPlan.PRO, service.upgrade("ana"));
        assertEquals(SubscriptionPlan.ENTERPRISE, service.upgrade("ana"));
        assertThrows(InvalidPlanChangeException.class, () -> service.upgrade("ana"));
    }

    @Test
    void keepsTheTokensAlreadyUsed() {
        InMemoryUserAccountRepository accounts = new InMemoryUserAccountRepository();
        accounts.findOrCreate("ana").consume(50_000L);
        new PlanUpgradeService(accounts).upgrade("ana");
        assertEquals(50_000L, accounts.findOrCreate("ana").tokensUsed());
        assertEquals(450_000L, accounts.findOrCreate("ana").tokensRemaining());
    }
}
