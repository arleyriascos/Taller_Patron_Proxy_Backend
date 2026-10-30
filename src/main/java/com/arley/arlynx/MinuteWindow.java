package com.arley.arlynx;

import java.time.Clock;
import org.springframework.stereotype.Component;

@Component
public class MinuteWindow {

    private static final long SECONDS_PER_MINUTE = 60L;

    private final Clock clock;

    public MinuteWindow(Clock clock) {
        this.clock = clock;
    }

    public long secondsUntilReset() {
        return SECONDS_PER_MINUTE - (clock.instant().getEpochSecond() % SECONDS_PER_MINUTE);
    }
}
