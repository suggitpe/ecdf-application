package org.acmebank.people.domain.service;

import org.acmebank.people.domain.*;
import org.acmebank.people.domain.port.AssessmentRepository;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.EvidenceRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final EvidenceRepository evidenceRepository;
    private final AssessmentRepository assessmentRepository;

    public CheckInService(
            CheckInRepository checkInRepository,
            EvidenceRepository evidenceRepository,
            AssessmentRepository assessmentRepository) {
        this.checkInRepository = checkInRepository;
        this.evidenceRepository = evidenceRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public CheckIn createCheckIn(UUID userId, UUID managerId, String managerNotes, Grade targetGrade, boolean isDraft) {
        LocalDate now = LocalDate.now();
        Map<Pillar, PillarScoreInfo> aggregatedScores = getAggregatedScores(userId);
        
        CheckInStatus status = CheckInStatus.DRAFT;
        if (!isDraft) {
            boolean hasItaAssessment = checkHasItaAssessment(userId);
            status = evaluateStatus(aggregatedScores, targetGrade, hasItaAssessment);
        }

        CheckIn checkIn = new CheckIn(
                null,
                userId,
                managerId,
                aggregatedScores,
                managerNotes,
                status,
                now
        );

        return checkInRepository.save(checkIn);
    }

    public CheckIn updateCheckIn(UUID checkInId, String managerNotes, Grade targetGrade, boolean finalize) {
        CheckIn existing = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new IllegalArgumentException("CheckIn not found: " + checkInId));
        
        if (existing.status() != CheckInStatus.DRAFT && existing.status() != null && finalize) {
            // Already finalized? usually shouldn't happen from UI logic but let's be safe
        }

        CheckInStatus newStatus = CheckInStatus.DRAFT;
        if (finalize) {
            boolean hasItaAssessment = checkHasItaAssessment(existing.userId());
            newStatus = evaluateStatus(existing.holisticScores(), targetGrade, hasItaAssessment);
        }

        CheckIn updated = new CheckIn(
                existing.id(),
                existing.userId(),
                existing.managerId(),
                existing.holisticScores(),
                managerNotes,
                newStatus,
                existing.checkInDate()
        );

        return checkInRepository.save(updated);
    }

    private boolean checkHasItaAssessment(UUID userId) {
        LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
        List<Evidence> assessedEvidence = new java.util.ArrayList<>(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED));
        assessedEvidence.addAll(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED));
        
        for (Evidence evidence : assessedEvidence) {
            if (evidence.createdDate().isBefore(threeYearsAgo)) continue;
            Optional<Assessment> assessmentOpt = assessmentRepository.findByEvidenceId(evidence.id());
            if (assessmentOpt.isPresent()) {
                Assessment assessment = assessmentOpt.get();
                if (assessment.assessmentDate() != null && assessment.assessmentDate().isBefore(threeYearsAgo)) continue;
                if (assessment.isThirdParty()) return true;
            }
        }
        return false;
    }

    public Map<Pillar, PillarScoreInfo> getAggregatedScores(UUID userId) {
        LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
        List<Evidence> assessedEvidence = new java.util.ArrayList<>(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED));
        assessedEvidence.addAll(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED));

        assessedEvidence.sort(Comparator.comparing(Evidence::createdDate).reversed());

        Map<Pillar, PillarScoreInfo> aggregatedScores = new EnumMap<>(Pillar.class);

        for (Evidence evidence : assessedEvidence) {
            if (evidence.createdDate().isBefore(threeYearsAgo)) {
                continue;
            }

            Optional<Assessment> assessmentOpt = assessmentRepository.findByEvidenceId(evidence.id());
            if (assessmentOpt.isPresent()) {
                Assessment assessment = assessmentOpt.get();

                if (assessment.assessmentDate() != null && assessment.assessmentDate().isBefore(threeYearsAgo)) {
                    continue;
                }

                if (assessment.assessedScores() != null) {
                    for (Map.Entry<Pillar, Score> entry : assessment.assessedScores().entrySet()) {
                        Pillar pillar = entry.getKey();
                        Score currentScore = entry.getValue();

                        if (!aggregatedScores.containsKey(pillar)) {
                            aggregatedScores.put(pillar, new PillarScoreInfo(currentScore, evidence.id()));
                        }
                    }
                }
            }
        }
        return aggregatedScores;
    }

    private CheckInStatus evaluateStatus(Map<Pillar, PillarScoreInfo> aggregatedScores, Grade targetGrade, boolean hasItaAssessment) {
        int belowExpectationsCount = 0;
        int aboveExpectationsCount = 0;

        for (Map.Entry<Pillar, Score> entry : targetGrade.expectations().entrySet()) {
            Pillar pillar = entry.getKey();
            Score expected = entry.getValue();
            PillarScoreInfo actualInfo = aggregatedScores.get(pillar);
            Score actual = actualInfo != null ? actualInfo.score() : null;

            if (actual == null || actual.value() < expected.value()) {
                belowExpectationsCount++;
            } else if (actual.value() > expected.value()) {
                aboveExpectationsCount++;
            }
        }

        if (belowExpectationsCount > 3) {
            return CheckInStatus.UNDERPERFORMING;
        }

        if (aboveExpectationsCount > 3) {
            return CheckInStatus.OVER_PERFORMING;
        }

        if (hasItaAssessment && belowExpectationsCount == 0) {
            return CheckInStatus.READY_FOR_PROMOTION;
        }

        return CheckInStatus.ON_TRACK;
    }
}
