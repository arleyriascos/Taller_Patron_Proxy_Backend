package com.arley.arlynx;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private static final int HISTORY_DAYS = 7;

    private final QuotaStatusProvider statusProvider;
    private final UsageHistory usageHistory;
    private final PlanUpgradeService upgradeService;

    public QuotaController(
            QuotaStatusProvider statusProvider,
            UsageHistory usageHistory,
            PlanUpgradeService upgradeService) {
        this.statusProvider = statusProvider;
        this.usageHistory = usageHistory;
        this.upgradeService = upgradeService;
    }

    @GetMapping("/status")
    public QuotaStatusResponse status(@RequestHeader("X-User-Id") String userId) {
        return statusProvider.statusFor(UserIdRules.require(userId));
    }

    @GetMapping("/history")
    public List<DailyUsage> history(@RequestHeader("X-User-Id") String userId) {
        return usageHistory.lastDays(UserIdRules.require(userId), HISTORY_DAYS);
    }

    @PostMapping("/upgrade")
    public QuotaStatusResponse upgrade(@RequestHeader("X-User-Id") String userId) {
        String validUserId = UserIdRules.require(userId);
        upgradeService.upgrade(validUserId);
        return statusProvider.statusFor(validUserId);
    }
}
