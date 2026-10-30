package com.arley.arlynx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MockAIGenerationServiceTest {

    @Test
    void waitsTheSimulatedLatencyAndReturnsTheSourceText() {
        MockAIGenerationService service =
                new MockAIGenerationService(() -> "abcdefgh", new CharacterRatioTokenEstimator());
        long start = System.nanoTime();
        GenerationResponse response = service.generate(new GenerationRequest("ana", "abcd"));
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;
        assertTrue(elapsedMillis >= 1200L);
        assertEquals("abcdefgh", response.text());
        assertEquals(1, response.promptTokens());
        assertEquals(2, response.completionTokens());
    }
}
