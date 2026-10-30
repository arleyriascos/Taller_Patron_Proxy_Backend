package com.arley.arlynx;

import java.time.LocalDate;

public record QuotaStatusResponse(
        SubscriptionPlan plan,
        long tokensUsed,
        long tokensRemaining,
        long tokenLimit,
        LocalDate resetDate,
        int requestsUsed,
        int requestLimit,
        long windowResetSeconds) {
}
