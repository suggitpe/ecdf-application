package org.acmebank.people.domain.service;

import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.port.EvidenceRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EvidenceService {

    private final EvidenceRepository evidenceRepository;

    public EvidenceService(EvidenceRepository evidenceRepository) {
        this.evidenceRepository = evidenceRepository;
    }

    public Evidence createEvidence(UUID userId, String title) {
        Evidence newEvidence = new Evidence(
                UUID.randomUUID(),
                userId,
                title,
                "",
                "",
                "",
                Collections.emptyMap(),
                Collections.emptyList(),
                Collections.emptyList(),
                EvidenceStatus.DRAFT,
                LocalDate.now(),
                LocalDate.now());
        return evidenceRepository.save(newEvidence);
    }

    public Evidence submitEvidence(UUID evidenceId) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found with ID: " + evidenceId));

        if (evidence.selfAssessment() == null || evidence.selfAssessment().isEmpty()) {
            throw new IllegalArgumentException("Self-assessment cannot be empty before submission.");
        }

        Evidence submittedEvidence = new Evidence(
                evidence.id(),
                evidence.userId(),
                evidence.title(),
                evidence.impact(),
                evidence.complexity(),
                evidence.contribution(),
                evidence.selfAssessment(),
                evidence.links(),
                evidence.attachmentPaths(),
                EvidenceStatus.SUBMITTED,
                evidence.createdDate(),
                LocalDate.now());

        return evidenceRepository.save(submittedEvidence);
    }

    public Evidence updateEvidence(UUID evidenceId, String title, String impact, String complexity, String contribution,
            Map<Pillar, Score> selfAssessment) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found with ID: " + evidenceId));

        if (evidence.status() == EvidenceStatus.SUBMITTED || evidence.status() == EvidenceStatus.ASSESSED) {
            throw new IllegalStateException("Cannot modify evidence that is already SUBMITTED or ASSESSED.");
        }

        Evidence updatedEvidence = new Evidence(
                evidence.id(),
                evidence.userId(),
                title,
                impact,
                complexity,
                contribution,
                selfAssessment,
                evidence.links(),
                evidence.attachmentPaths(),
                evidence.status(),
                evidence.createdDate(),
                LocalDate.now());

        return evidenceRepository.save(updatedEvidence);
    }
}
