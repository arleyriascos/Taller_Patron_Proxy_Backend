package com.arley.arlynx;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiGenerationController {

    private final AIGenerationService generationService;

    public AiGenerationController(AIGenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/generate")
    public GenerationResponse generate(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody PromptPayload payload) {
        return generationService.generate(new GenerationRequest(userId, payload.prompt()));
    }
}
