package com.arley.arlynx;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsageHistory implements UsageHistory {

    private final Clock clock;
    private final Map<String, Map<LocalDate, LongAdder>> usageByUser = new ConcurrentHashMap<>();

    public InMemoryUsageHistory(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void record(String userId, long tokens) {
        usageByUser
                .computeIfAbsent(userId, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(LocalDate.now(clock), key -> new LongAdder())
                .add(tokens);
    }

    @Override
    public List<DailyUsage> lastDays(String userId, int days) {
        Map<LocalDate, LongAdder> perDay = usageByUser.getOrDefault(userId, Map.of());
        LocalDate today = LocalDate.now(clock);
        return IntStream.range(0, days)
                .mapToObj(offset -> today.minusDays(days - 1L - offset))
                .map(day -> new DailyUsage(day, perDay.containsKey(day) ? perDay.get(day).sum() : 0L))
                .toList();
    }
}
