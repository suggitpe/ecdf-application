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

        if (evidence.status() != EvidenceStatus.MANAGER_ASSESSED) {
            throw new IllegalStateException("Can only assign ITA to evidence that has been MANAGER_ASSESSED.");
        }

        // Check if already assigned or assessed by ITA
        java.util.List<Assessment> existing = assessmentRepository.findByEvidenceId(evidenceId);
        if (existing.stream().anyMatch(Assessment::isThirdParty)) {
            throw new IllegalStateException("This evidence is already assigned or assessed by an ITA.");
        }

        Assessment pendingAssessment = new Assessment(
                null,
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

        if (scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException("Assessment scores cannot be empty.");
        }

        if (reviewSummary == null || reviewSummary.trim().isEmpty()) {
            throw new IllegalArgumentException("Review summary must be provided.");
        }

        java.util.List<Assessment> assessments = assessmentRepository.findByEvidenceId(evidenceId);
        Optional<Assessment> pendingItaAssessment = assessments.stream()
                .filter(a -> a.isThirdParty() && a.assessmentDate() == null)
                .findFirst();

        EvidenceStatus targetStatus;
        Assessment assessmentToSave;

        if (pendingItaAssessment.isPresent()) {
            // ITA Assessment
            if (evidence.status() != EvidenceStatus.MANAGER_ASSESSED) {
                throw new IllegalStateException("ITA can only assess evidence in MANAGER_ASSESSED state.");
            }
            Assessment pending = pendingItaAssessment.get();
            assessmentToSave = new Assessment(
                    pending.id(),
                    evidenceId,
                    assessorId,
                    scores,
                    reviewSummary,
                    true,
                    LocalDate.now()
            );
            targetStatus = EvidenceStatus.INDEPENDENTLY_ASSESSED;
        } else {
            // Manager Assessment
            if (evidence.status() != EvidenceStatus.SUBMITTED) {
                throw new IllegalStateException("Manager can only assess evidence in SUBMITTED state.");
            }
            assessmentToSave = new Assessment(
                    null,
                    evidenceId,
                    assessorId,
                    scores,
                    reviewSummary,
                    false,
                    LocalDate.now()
            );
            targetStatus = EvidenceStatus.MANAGER_ASSESSED;
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
                targetStatus,
                evidence.createdDate(),
                LocalDate.now()
        );
        evidenceRepository.save(assessedEvidence);

        return savedAssessment;
    }
}
