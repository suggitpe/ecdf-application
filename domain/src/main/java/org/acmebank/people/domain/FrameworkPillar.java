package org.acmebank.people.domain;

import java.util.List;

public record FrameworkPillar(
        Pillar pillar,
        String title,
        String description,
        List<FrameworkLevel> levels
) {
}
