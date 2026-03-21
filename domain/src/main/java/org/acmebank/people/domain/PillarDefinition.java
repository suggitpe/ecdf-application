package org.acmebank.people.domain;

import java.util.List;

public record PillarDefinition(
    Pillar pillar,
    String title,
    String description,
    List<PillarLevelDetail> levelDetails
) {}
