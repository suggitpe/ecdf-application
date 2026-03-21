package org.acmebank.people.domain;

import java.util.UUID;

public record FrameworkLevel(
        UUID id,
        Pillar pillar,
        Score score,
        String levelDescription,
        String evidenceExamples
) {
}
