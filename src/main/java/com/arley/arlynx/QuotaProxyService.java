package com.arley.arlynx;

public class QuotaProxyService implements AIGenerationService {

    private final AIGenerationService delegate;
    private final UserAccountRepository accounts;
    private final TokenEstimator estimator;
    private final UsageHistory history;

    public QuotaProxyService(
            AIGenerationService delegate,
            UserAccountRepository accounts,
            TokenEstimator estimator,
            UsageHistory history) {
        this.delegate = delegate;
        this.accounts = accounts;
        this.estimator = estimator;
        this.history = history;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount account = accounts.findOrCreate(request.userId());
        if (!account.canAfford(estimator.estimate(request.prompt()))) {
            throw new QuotaExhaustedException();
        }
        GenerationResponse response = delegate.generate(request);
        long charged = (long) response.promptTokens() + response.completionTokens();
        history.record(request.userId(), account.consume(charged));
        return response;
    }
}
