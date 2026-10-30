package com.arley.arlynx;

import java.time.LocalDate;

public record DailyUsage(LocalDate date, long tokens) {
}
