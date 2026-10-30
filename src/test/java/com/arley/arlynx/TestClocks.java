package com.arley.arlynx;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

final class TestClocks {

    private TestClocks() {
    }

    static Clock at(String isoInstant) {
        return Clock.fixed(Instant.parse(isoInstant), ZoneOffset.UTC);
    }
}
