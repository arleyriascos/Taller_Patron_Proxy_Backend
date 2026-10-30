package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuotaProxyServiceTest {

    private InMemoryUserAccountRepository accounts;
    private InMemoryUsageHistory history;
    private RecordingGenerationService real;
    private QuotaProxyService proxy;

    @BeforeEach
    void setUp() {
        accounts = new InMemoryUserAccountRepository();
        history = new InMemoryUsageHistory(TestClocks.at("2026-10-30T10:00:00Z"));
        real = new RecordingGenerationService();
        proxy = new QuotaProxyService(real, accounts, new CharacterRatioTokenEstimator(), history);
    }

    private GenerationRequest requestWith(String prompt) {
        return new GenerationRequest("ana", prompt);
    }

    @Test
    void chargesPromptAndCompletionTokens() {
        proxy.generate(requestWith("hello"));
        assertEquals(100L, accounts.findOrCreate("ana").tokensUsed());
        assertEquals(49_900L, accounts.findOrCreate("ana").tokensRemaining());
    }

    @Test
    void rejectsWhenTheQuotaIsExhausted() {
        accounts.findOrCreate("ana").consume(50_000L);
        assertThrows(QuotaExhaustedException.class, () -> proxy.generate(requestWith("hello")));
        assertEquals(0, real.calls());
    }

    @Test
    void rejectsWhenThePromptDoesNotFitInTheBalance() {
        accounts.findOrCreate("ana").consume(49_995L);
        assertThrows(QuotaExhaustedException.class, () -> proxy.generate(requestWith("x".repeat(40))));
        assertEquals(0, real.calls());
    }

    @Test
    void neverChargesMoreThanThePlanLimit() {
        accounts.findOrCreate("ana").consume(49_950L);
        proxy.generate(requestWith("hello"));
        assertEquals(50_000L, accounts.findOrCreate("ana").tokensUsed());
        assertEquals(0L, accounts.findOrCreate("ana").tokensRemaining());
    }

    @Test
    void upgradingRestoresHeadroom() {
        accounts.findOrCreate("ana").consume(50_000L);
        accounts.findOrCreate("ana").upgradePlan();
        proxy.generate(requestWith("hello"));
        assertEquals(1, real.calls());
    }

    @Test
    void enterprisePlanIgnoresTheQuota() {
        UserAccount account = accounts.findOrCreate("ana");
        account.upgradePlan();
        account.upgradePlan();
        account.consume(10_000_000L);
        proxy.generate(requestWith("hello"));
        assertEquals(-1L, account.tokensRemaining());
        assertEquals(1, real.calls());
    }

    @Test
    void recordsTheDailyUsage() {
        proxy.generate(requestWith("hello"));
        proxy.generate(requestWith("hello"));
        assertEquals(200L, history.lastDays("ana", 7).get(6).tokens());
    }

    @Test
    void doesNotChargeWhenTheRealServiceFails() {
        AIGenerationService failing = request -> {
            throw new GenerationInterruptedException();
        };
        QuotaProxyService failingProxy =
                new QuotaProxyService(failing, accounts, new CharacterRatioTokenEstimator(), history);
        assertThrows(GenerationInterruptedException.class, () -> failingProxy.generate(requestWith("hello")));
        assertEquals(0L, accounts.findOrCreate("ana").tokensUsed());
    }
}
