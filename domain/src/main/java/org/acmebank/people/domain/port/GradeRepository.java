package org.acmebank.people.domain.port;

import org.acmebank.people.domain.Grade;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface GradeRepository {
    Grade save(Grade grade);

    Optional<Grade> findById(UUID id);

    Optional<Grade> findByNameAndRole(String name, String role);

    List<Grade> findAll();

    void updateExpectations(UUID gradeId, java.util.Map<org.acmebank.people.domain.Pillar, org.acmebank.people.domain.Score> expectations);
}
