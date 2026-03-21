package org.acmebank.people.domain.port;

import org.acmebank.people.domain.FrameworkPillar;
import org.acmebank.people.domain.Pillar;

import java.util.List;
import java.util.Optional;

public interface FrameworkRepository {
    List<FrameworkPillar> findAllPillars();
    Optional<FrameworkPillar> findPillar(Pillar pillar);
    FrameworkPillar savePillar(FrameworkPillar pillar);
}
