package com.arley.arlynx;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateWindowResetTask {

    private final RequestWindowTracker tracker;

    public RateWindowResetTask(RequestWindowTracker tracker) {
        this.tracker = tracker;
    }

    @Scheduled(cron = "0 * * * * *", zone = "UTC")
    public void resetWindow() {
        tracker.clear();
    }
}
