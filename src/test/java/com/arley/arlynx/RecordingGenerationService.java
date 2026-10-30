package com.arley.arlynx;

import java.util.concurrent.atomic.AtomicInteger;

class RecordingGenerationService implements AIGenerationService {

    private final AtomicInteger calls = new AtomicInteger();

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        calls.incrementAndGet();
        return new GenerationResponse("ok", 10, 90);
    }

    int calls() {
        return calls.get();
    }
}
