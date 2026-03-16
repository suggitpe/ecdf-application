package org.acmebank.people.domain.service;

import org.acmebank.people.domain.Assessment;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.port.AssessmentRepository;
import org.acmebank.people.domain.port.EvidenceRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final EvidenceRepository evidenceRepository;

    public AssessmentService(AssessmentRepository assessmentRepository, EvidenceRepository evidenceRepository) {
        this.assessmentRepository = assessmentRepository;
        this.evidenceRepository = evidenceRepository;
    }

    public Assessment assignThirdPartyAssessor(UUID evidenceId, UUID assessorId) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found with ID: " + evidenceId));

        if (evidence.status() != EvidenceStatus.SUBMITTED) {
            throw new IllegalStateException("Can only assign assessor to SUBMITTED evidence.");
        }

        Assessment pendingAssessment = new Assessment(
                UUID.randomUUID(),
                evidenceId,
                assessorId,
                null,
                null,
                true,
                null);

        return assessmentRepository.save(pendingAssessment);
    }

    public Assessment submitAssessment(UUID evidenceId, UUID assessorId, Map<Pillar, Score> scores, String reviewSummary) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found with ID: " + evidenceId));

        if (evidence.status() != EvidenceStatus.SUBMITTED) {
            throw new IllegalStateException("Can only assess evidence that is in SUBMITTED state.");
        }

        if (scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException("Assessment scores cannot be empty.");
        }

        if (reviewSummary == null || reviewSummary.trim().isEmpty()) {
            throw new IllegalArgumentException("Review summary must be provided.");
        }

        Optional<Assessment> existingPendingAssessmentOpt = assessmentRepository.findByEvidenceId(evidenceId);
        
        Assessment assessmentToSave;
        if (existingPendingAssessmentOpt.isPresent()) {
            Assessment pending = existingPendingAssessmentOpt.get();
            assessmentToSave = new Assessment(
                    pending.id(),
                    pending.evidenceId(),
                    pending.assessorId(),
                    scores,
                    reviewSummary,
                    pending.isThirdParty(),
                    LocalDate.now()
            );
        } else {
            assessmentToSave = new Assessment(
                    UUID.randomUUID(),
                    evidenceId,
                    assessorId,
                    scores,
                    reviewSummary,
                    false, // Direct manager assessment
                    LocalDate.now()
            );
        }

        Assessment savedAssessment = assessmentRepository.save(assessmentToSave);

        Evidence assessedEvidence = new Evidence(
                evidence.id(),
                evidence.userId(),
                evidence.title(),
                evidence.description(),
                evidence.impact(),
                evidence.complexity(),
                evidence.contribution(),
                evidence.selfAssessment(),
                evidence.links(),
                evidence.attachmentPaths(),
                EvidenceStatus.ASSESSED,
                evidence.createdDate(),
                LocalDate.now()
        );
        evidenceRepository.save(assessedEvidence);

        return savedAssessment;
    }
}
