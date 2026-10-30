package com.arley.arlynx;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomCannedTextSource implements TextSource {

    private static final List<String> TEXTS = List.of(
            "The Proxy pattern places a stand-in object in front of a real one so that every call can be inspected before it arrives. "
                    + "The stand-in implements the same interface as the real object, which means callers never notice the difference. "
                    + "That property is what makes proxies composable: one proxy can wrap another, and each one adds a single concern such as "
                    + "access control, caching, logging or throttling. The real object stays focused on its own job and remains easy to test.",
            "Rate limiting protects a service from bursts of traffic. A fixed window counts the requests of each caller during a short period "
                    + "and rejects the extra ones until the window rolls over. Well behaved servers answer with a clear status and tell the client "
                    + "how long to wait, so that the client can pause instead of retrying blindly. Good interfaces turn that waiting time into a "
                    + "visible countdown so that people understand what is happening.",
            "A monthly token quota turns usage into a budget. Every request spends part of that budget, and the remaining balance decides "
                    + "whether the next request is allowed. When the budget reaches zero the server answers that payment is required, which is a "
                    + "natural moment to offer a higher plan. Keeping the balance in a single place makes it easy to display progress and to reset "
                    + "everything when a new billing cycle begins.",
            "Chaining decorators and proxies is like stacking filters. The outer layer sees the request first and may reject it immediately. "
                    + "Only the requests that survive every layer reach the real service, so expensive work is never wasted on traffic that was "
                    + "going to be refused anyway. The order matters, and it should be decided in exactly one place of the application so that "
                    + "nobody has to guess how the pipeline was assembled.",
            "Simulated services are valuable during development. A mock that sleeps for a moment and returns predefined text behaves like a "
                    + "slow remote model, which lets the team build loading indicators, error states and quota displays long before a real "
                    + "provider is connected. When the real integration arrives, only the object behind the interface changes and the rest of the "
                    + "application keeps working untouched.");

    @Override
    public String nextText() {
        return TEXTS.get(ThreadLocalRandom.current().nextInt(TEXTS.size()));
    }
}
