package org.acmebank.people.application.adapter.out.persistence.repository;

import org.acmebank.people.application.adapter.out.persistence.entity.AssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentJpaRepository extends JpaRepository<AssessmentEntity, UUID> {
    Optional<AssessmentEntity> findByEvidenceId(UUID evidenceId);

    List<AssessmentEntity> findByAssessorId(UUID assessorId);
    
    List<AssessmentEntity> findByAssessorIdAndAssessmentDateIsNull(UUID assessorId);
}
