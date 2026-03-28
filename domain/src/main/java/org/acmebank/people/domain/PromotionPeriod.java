package org.acmebank.people.domain;

import java.time.LocalDate;
import java.util.UUID;

public record PromotionPeriod(
        UUID id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        PromotionPeriodStatus status) {
}
