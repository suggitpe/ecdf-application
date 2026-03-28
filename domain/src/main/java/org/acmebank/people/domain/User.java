package org.acmebank.people.domain;

import java.util.UUID;

public record User(UUID id, String email, String fullName, Grade grade, UUID managerId, boolean isIta, boolean isPromotionCoordinator) {

    public Score getExpectedPillarLevel(Pillar pillar) {
        if (grade == null) {
            return new Score(1); // Default minimal score if no grade
        }
        return grade.getExpectationFor(pillar);
    }
}
