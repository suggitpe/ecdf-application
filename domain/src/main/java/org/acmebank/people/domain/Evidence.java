package org.acmebank.people.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record Evidence(
        UUID id,
        UUID userId,
        String title,
        String description,
        String impact,
        String complexity,
        String contribution,
        Map<Pillar, EvidenceRating> selfAssessment,
        List<String> links,
        List<String> attachmentPaths,
        EvidenceStatus status,
        LocalDate createdDate,
        LocalDate lastModifiedDate) {
}
