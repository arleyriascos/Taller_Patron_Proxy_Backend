package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateLimitProxyServiceTest {

    private InMemoryUserAccountRepository accounts;
    private InMemoryRequestWindowTracker tracker;
    private RecordingGenerationService real;
    private RateLimitProxyService proxy;

    @BeforeEach
    void setUp() {
        accounts = new InMemoryUserAccountRepository();
        tracker = new InMemoryRequestWindowTracker();
        real = new RecordingGenerationService();
        MinuteWindow window = new MinuteWindow(TestClocks.at("2026-10-30T10:00:20Z"));
        proxy = new RateLimitProxyService(real, accounts, tracker, window);
    }

    private GenerationRequest requestFor(String userId) {
        return new GenerationRequest(userId, "hello");
    }

    @Test
    void allowsRequestsUpToThePlanLimit() {
        for (int i = 0; i < 10; i++) {
            proxy.generate(requestFor("ana"));
        }
        assertEquals(10, real.calls());
    }

    @Test
    void rejectsTheRequestAboveTheLimitWithTheSecondsToWait() {
        for (int i = 0; i < 10; i++) {
            proxy.generate(requestFor("ana"));
        }
        RateLimitExceededException exception =
                assertThrows(RateLimitExceededException.class, () -> proxy.generate(requestFor("ana")));
        assertEquals(40L, exception.retryAfterSeconds());
        assertEquals(10, real.calls());
    }

    @Test
    void proPlanAllowsSixtyRequestsPerMinute() {
        accounts.findOrCreate("ana").upgradePlan();
        for (int i = 0; i < 60; i++) {
            proxy.generate(requestFor("ana"));
        }
        assertThrows(RateLimitExceededException.class, () -> proxy.generate(requestFor("ana")));
        assertEquals(60, real.calls());
    }

    @Test
    void enterprisePlanIsNeverRateLimited() {
        accounts.findOrCreate("ana").upgradePlan();
        accounts.findOrCreate("ana").upgradePlan();
        for (int i = 0; i < 200; i++) {
            proxy.generate(requestFor("ana"));
        }
        assertEquals(200, real.calls());
    }

    @Test
    void usersHaveIndependentWindows() {
        for (int i = 0; i < 10; i++) {
            proxy.generate(requestFor("ana"));
        }
        proxy.generate(requestFor("luis"));
        assertEquals(11, real.calls());
    }

    @Test
    void clearingTheWindowAllowsNewRequests() {
        for (int i = 0; i < 10; i++) {
            proxy.generate(requestFor("ana"));
        }
        tracker.clear();
        proxy.generate(requestFor("ana"));
        assertEquals(11, real.calls());
    }
}
