package org.acmebank.people.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record Assessment(
        UUID id,
        UUID evidenceId,
        UUID assessorId,
        Map<Pillar, EvidenceRating> assessedScores,
        String reviewSummary,
        boolean isThirdParty,
        LocalDate assessmentDate) {
}
