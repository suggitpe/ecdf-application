package org.acmebank.people.domain.port;

import org.acmebank.people.domain.Assessment;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface AssessmentRepository {
    Assessment save(Assessment assessment);

    Optional<Assessment> findById(UUID id);

    Optional<Assessment> findByEvidenceId(UUID evidenceId);

    List<Assessment> findByAssessorId(UUID assessorId);

    List<Assessment> findPendingByAssessorId(UUID assessorId);
}
