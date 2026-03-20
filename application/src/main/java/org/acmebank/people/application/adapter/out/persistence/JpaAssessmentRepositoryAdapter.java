package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.AssessmentEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.EvidenceEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.AssessmentJpaRepository;
import org.acmebank.people.application.adapter.out.persistence.repository.EvidenceJpaRepository;
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository;
import org.acmebank.people.domain.Assessment;
import org.acmebank.people.domain.port.AssessmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaAssessmentRepositoryAdapter implements AssessmentRepository {

    private final AssessmentJpaRepository assessmentJpaRepository;
    private final EvidenceJpaRepository evidenceJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Assessment save(Assessment assessment) {
        java.util.Objects.requireNonNull(assessment.evidenceId(), "Evidence ID must not be null");
        java.util.Objects.requireNonNull(assessment.assessorId(), "Assessor ID must not be null");
        EvidenceEntity evidenceEntity = evidenceJpaRepository.findById(assessment.evidenceId())
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + assessment.evidenceId()));

        UserEntity assessorEntity = userJpaRepository.findById(assessment.assessorId())
                .orElseThrow(() -> new IllegalArgumentException("Assessor not found: " + assessment.assessorId()));

        AssessmentEntity entity;
        if (assessment.id() != null) {
            entity = assessmentJpaRepository.findById(assessment.id())
                    .or(() -> assessmentJpaRepository.findByEvidenceId(assessment.evidenceId()))
                    .orElse(new AssessmentEntity());
        } else {
            entity = assessmentJpaRepository.findByEvidenceId(assessment.evidenceId())
                    .orElse(new AssessmentEntity());
        }

        DomainPersistenceMapper.updateAssessmentEntity(entity, assessment, evidenceEntity, assessorEntity);
        AssessmentEntity saved = assessmentJpaRepository.save(entity);
        assessmentJpaRepository.flush();
        return DomainPersistenceMapper.toDomainAssessment(saved);
    }

    @Override
    public Optional<Assessment> findById(UUID id) {
        return assessmentJpaRepository.findById(id)
                .map(DomainPersistenceMapper::toDomainAssessment);
    }

    @Override
    public Optional<Assessment> findByEvidenceId(UUID evidenceId) {
        return assessmentJpaRepository.findByEvidenceId(evidenceId)
                .map(DomainPersistenceMapper::toDomainAssessment);
    }

    @Override
    public List<Assessment> findByAssessorId(UUID assessorId) {
        return assessmentJpaRepository.findByAssessorId(assessorId).stream()
                .map(DomainPersistenceMapper::toDomainAssessment)
                .collect(Collectors.toList());
    }

    @Override
    public List<Assessment> findPendingByAssessorId(UUID assessorId) {
        return assessmentJpaRepository.findByAssessorIdAndAssessmentDateIsNull(assessorId).stream()
                .map(DomainPersistenceMapper::toDomainAssessment)
                .collect(Collectors.toList());
    }
}
