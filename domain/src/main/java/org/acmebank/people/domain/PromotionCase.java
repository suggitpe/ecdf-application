package org.acmebank.people.domain;

import java.util.UUID;

public record PromotionCase(
        UUID id,
        UUID candidateId,
        UUID managerId,
        UUID targetGradeId,
        UUID promotionPeriodId,
        String rationale,
        PromotionStatus status) {
}
