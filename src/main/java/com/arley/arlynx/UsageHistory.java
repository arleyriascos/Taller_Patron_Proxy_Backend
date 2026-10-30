package com.arley.arlynx;

import java.util.List;

public interface UsageHistory {

    void record(String userId, long tokens);

    List<DailyUsage> lastDays(String userId, int days);
}
