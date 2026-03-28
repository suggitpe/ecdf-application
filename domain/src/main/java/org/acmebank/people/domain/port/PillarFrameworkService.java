package org.acmebank.people.domain.port;

import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.PillarDefinition;

import java.util.List;
import java.util.Optional;

public interface PillarFrameworkService {
    List<PillarDefinition> getAllDefinitions();
    Optional<PillarDefinition> getDefinition(Pillar pillar);
    void updateDefinition(PillarDefinition definition);
}
