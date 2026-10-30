package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuotaStatusProviderTest {

    private InMemoryUserAccountRepository accounts;
    private InMemoryRequestWindowTracker tracker;
    private QuotaStatusProvider provider;

    @BeforeEach
    void setUp() {
        accounts = new InMemoryUserAccountRepository();
        tracker = new InMemoryRequestWindowTracker();
        var clock = TestClocks.at("2026-12-15T08:00:45Z");
        provider = new QuotaStatusProvider(accounts, tracker, new MinuteWindow(clock), new BillingCycle(clock));
    }

    @Test
    void reportsTheFreePlanStateOfANewUser() {
        QuotaStatusResponse status = provider.statusFor("ana");
        assertEquals(SubscriptionPlan.FREE, status.plan());
        assertEquals(0L, status.tokensUsed());
        assertEquals(50_000L, status.tokensRemaining());
        assertEquals(50_000L, status.tokenLimit());
        assertEquals(LocalDate.of(2027, 1, 1), status.resetDate());
        assertEquals(10, status.requestLimit());
        assertEquals(15L, status.windowResetSeconds());
    }

    @Test
    void reportsRequestsUsedInTheCurrentWindow() {
        tracker.tryAcquire("ana", 10);
        tracker.tryAcquire("ana", 10);
        assertEquals(2, provider.statusFor("ana").requestsUsed());
    }

    @Test
    void reportsUnlimitedValuesAsMinusOneForEnterprise() {
        accounts.findOrCreate("ana").upgradePlan();
        accounts.findOrCreate("ana").upgradePlan();
        QuotaStatusResponse status = provider.statusFor("ana");
        assertEquals(-1L, status.tokensRemaining());
        assertEquals(-1L, status.tokenLimit());
        assertEquals(-1, status.requestLimit());
    }
}
