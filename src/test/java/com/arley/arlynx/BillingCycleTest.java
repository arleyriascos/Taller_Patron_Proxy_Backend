package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BillingCycleTest {

    @Test
    void resetsOnTheFirstDayOfTheNextMonth() {
        assertEquals(LocalDate.of(2026, 11, 1), new BillingCycle(TestClocks.at("2026-10-30T10:00:00Z")).nextResetDate());
    }

    @Test
    void rollsOverTheYear() {
        assertEquals(LocalDate.of(2027, 1, 1), new BillingCycle(TestClocks.at("2026-12-31T23:59:59Z")).nextResetDate());
    }

    @Test
    void onTheFirstDayTheNextResetIsTheFollowingMonth() {
        assertEquals(LocalDate.of(2026, 2, 1), new BillingCycle(TestClocks.at("2026-01-01T00:00:00Z")).nextResetDate());
    }
}
