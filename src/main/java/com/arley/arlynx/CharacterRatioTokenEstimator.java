package com.arley.arlynx;

import org.springframework.stereotype.Component;

@Component
public class CharacterRatioTokenEstimator implements TokenEstimator {

    private static final int CHARACTERS_PER_TOKEN = 4;

    @Override
    public int estimate(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (text.strip().length() + CHARACTERS_PER_TOKEN - 1) / CHARACTERS_PER_TOKEN;
    }
}
