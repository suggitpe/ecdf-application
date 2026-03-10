package org.acmebank.people.domain.port;

import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface EvidenceRepository {
    Evidence save(Evidence evidence);

    Optional<Evidence> findById(UUID id);

    List<Evidence> findByUserId(UUID userId);

    List<Evidence> findByUserIdAndStatus(UUID userId, EvidenceStatus status);
}
