package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProxyChainOrderTest {

    private InMemoryUserAccountRepository accounts;
    private InMemoryRequestWindowTracker tracker;
    private InMemoryUsageHistory history;
    private MinuteWindow window;
    private RecordingGenerationService real;
    private AIGenerationService chain;

    @BeforeEach
    void setUp() {
        accounts = new InMemoryUserAccountRepository();
        tracker = new InMemoryRequestWindowTracker();
        history = new InMemoryUsageHistory(TestClocks.at("2026-10-30T10:00:00Z"));
        window = new MinuteWindow(TestClocks.at("2026-10-30T10:00:00Z"));
        real = new RecordingGenerationService();
        AIGenerationService quota =
                new QuotaProxyService(real, accounts, new CharacterRatioTokenEstimator(), history);
        chain = new RateLimitProxyService(quota, accounts, tracker, window);
    }

    private GenerationRequest request() {
        return new GenerationRequest("ana", "hello");
    }

    @Test
    void successfulRequestTraversesRateLimitThenQuotaThenRealService() {
        chain.generate(request());
        assertEquals(1, tracker.used("ana"));
        assertEquals(100L, accounts.findOrCreate("ana").tokensUsed());
        assertEquals(1, real.calls());
    }

    @Test
    void rateLimitIsEvaluatedBeforeTheQuota() {
        accounts.findOrCreate("ana").consume(50_000L);
        for (int i = 0; i < 10; i++) {
            tracker.tryAcquire("ana", 10);
        }
        assertThrows(RateLimitExceededException.class, () -> chain.generate(request()));
    }

    @Test
    void quotaIsEvaluatedAfterTheRateLimitCountedTheRequest() {
        accounts.findOrCreate("ana").consume(50_000L);
        assertThrows(QuotaExhaustedException.class, () -> chain.generate(request()));
        assertEquals(1, tracker.used("ana"));
        assertEquals(0, real.calls());
    }

    @Test
    void rateLimitedRequestNeverReachesTheQuotaOrTheRealService() {
        for (int i = 0; i < 10; i++) {
            chain.generate(request());
        }
        assertThrows(RateLimitExceededException.class, () -> chain.generate(request()));
        assertEquals(1_000L, accounts.findOrCreate("ana").tokensUsed());
        assertEquals(10, real.calls());
    }

    @Test
    void configurationAssemblesTheChainAroundTheMockService() {
        AIGenerationService assembled = new ProxyChainConfiguration().aiGenerationService(
                new RandomCannedTextSource(),
                new CharacterRatioTokenEstimator(),
                accounts,
                tracker,
                window,
                history);
        GenerationResponse response = assembled.generate(request());
        assertFalse(response.text().isBlank());
        assertEquals(1, tracker.used("ana"));
        assertEquals(response.promptTokens() + response.completionTokens(), accounts.findOrCreate("ana").tokensUsed());
    }
}
