package com.arley.arlynx;

public class MockAIGenerationService implements AIGenerationService {

    private static final long SIMULATED_LATENCY_MILLIS = 1200L;

    private final TextSource textSource;
    private final TokenEstimator tokenEstimator;

    public MockAIGenerationService(TextSource textSource, TokenEstimator tokenEstimator) {
        this.textSource = textSource;
        this.tokenEstimator = tokenEstimator;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        simulateLatency();
        String text = textSource.nextText();
        return new GenerationResponse(
                text,
                tokenEstimator.estimate(request.prompt()),
                tokenEstimator.estimate(text));
    }

    private void simulateLatency() {
        try {
            Thread.sleep(SIMULATED_LATENCY_MILLIS);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new GenerationInterruptedException();
        }
    }
}
