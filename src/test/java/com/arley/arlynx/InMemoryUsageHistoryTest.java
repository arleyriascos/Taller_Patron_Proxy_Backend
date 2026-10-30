package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class InMemoryUsageHistoryTest {

    private final InMemoryUsageHistory history = new InMemoryUsageHistory(TestClocks.at("2026-10-30T10:00:00Z"));

    @Test
    void returnsSevenAscendingDaysEndingToday() {
        List<DailyUsage> days = history.lastDays("ana", 7);
        assertEquals(7, days.size());
        assertEquals(LocalDate.of(2026, 10, 24), days.get(0).date());
        assertEquals(LocalDate.of(2026, 10, 30), days.get(6).date());
    }

    @Test
    void fillsMissingDaysWithZero() {
        history.record("ana", 120L);
        List<DailyUsage> days = history.lastDays("ana", 7);
        assertEquals(0L, days.get(0).tokens());
        assertEquals(120L, days.get(6).tokens());
    }

    @Test
    void keepsUsersSeparated() {
        history.record("ana", 50L);
        assertEquals(0L, history.lastDays("luis", 7).get(6).tokens());
    }
}
