package org.acmebank.people.domain;

import java.util.UUID;

public record Sponsorship(
        UUID id,
        UUID promotionCaseId,
        UUID sponsorId,
        SponsorshipStatus status,
        String writtenAccount) {
}
