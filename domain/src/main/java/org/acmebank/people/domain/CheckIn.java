package org.acmebank.people.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record CheckIn(
        UUID id,
        UUID userId,
        UUID managerId,
        Map<Pillar, Score> holisticScores,
        String managerNotes,
        CheckInStatus status,
        LocalDate checkInDate) {
}
