package org.acmebank.people.domain;

import java.util.List;

public record PillarLevelDetail(
    int level,
    String description,
    List<String> examples
) {}
