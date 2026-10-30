package com.arley.arlynx;

import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class BillingCycle {

    private final Clock clock;

    public BillingCycle(Clock clock) {
        this.clock = clock;
    }

    public LocalDate nextResetDate() {
        return LocalDate.now(clock).withDayOfMonth(1).plusMonths(1);
    }
}
