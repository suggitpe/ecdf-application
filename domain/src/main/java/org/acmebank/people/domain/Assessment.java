package org.acmebank.people.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record Assessment(
        UUID id,
        UUID evidenceId,
        UUID assessorId,
        Map<Pillar, Score> assessedScores,
        String reviewSummary,
        boolean isThirdParty,
        LocalDate assessmentDate) {

    public Assessment {
        if (assessmentDate != null) {
            if (assessedScores == null || assessedScores.isEmpty()) {
                throw new IllegalArgumentException("Assessed scores must not be empty");
            }
            if (reviewSummary == null || reviewSummary.trim().isEmpty()) {
                throw new IllegalArgumentException("Review summary must not be blank");
            }
        }
    }
}
