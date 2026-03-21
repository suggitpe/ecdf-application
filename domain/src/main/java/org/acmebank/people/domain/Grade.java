package org.acmebank.people.domain;

import java.util.Map;
import java.util.UUID;

public record Grade(UUID id, String name, String role, Map<Pillar, Score> expectations) {

    public Grade {
        if (expectations == null) {
            expectations = new java.util.HashMap<>();
        } else {
            expectations = new java.util.HashMap<>(expectations);
        }

        expectations = java.util.Collections.unmodifiableMap(expectations);
    }

    public Score getExpectationFor(Pillar pillar) {
        return expectations.getOrDefault(pillar, new Score(1));
    }
}
