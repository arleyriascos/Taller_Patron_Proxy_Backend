package com.arley.arlynx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class InMemoryRequestWindowTracker implements RequestWindowTracker {

    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    @Override
    public boolean tryAcquire(String userId, int limit) {
        AtomicInteger counter = counters.computeIfAbsent(userId, key -> new AtomicInteger());
        while (true) {
            int current = counter.get();
            if (current >= limit) {
                return false;
            }
            if (counter.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    @Override
    public int used(String userId) {
        AtomicInteger counter = counters.get(userId);
        return counter == null ? 0 : counter.get();
    }

    @Override
    public void clear() {
        counters.clear();
    }
}
