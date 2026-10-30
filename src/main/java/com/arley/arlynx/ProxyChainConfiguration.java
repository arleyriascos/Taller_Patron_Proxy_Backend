package com.arley.arlynx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyChainConfiguration {

    @Bean
    public AIGenerationService aiGenerationService(
            TextSource textSource,
            TokenEstimator tokenEstimator,
            UserAccountRepository accounts,
            RequestWindowTracker tracker,
            MinuteWindow window,
            UsageHistory history) {
        AIGenerationService real = new MockAIGenerationService(textSource, tokenEstimator);
        AIGenerationService quota = new QuotaProxyService(real, accounts, tokenEstimator, history);
        return new RateLimitProxyService(quota, accounts, tracker, window);
    }
}
