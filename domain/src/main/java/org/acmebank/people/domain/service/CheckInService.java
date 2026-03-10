package org.acmebank.people.domain.service;

import org.acmebank.people.domain.Assessment;
import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.CheckInStatus;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.port.AssessmentRepository;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.EvidenceRepository;

import java.time.LocalDate;
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

    public CheckIn createCheckIn(UUID userId, UUID managerId, String managerNotes, Grade targetGrade) {
        LocalDate now = LocalDate.now();
        LocalDate threeYearsAgo = now.minusYears(3);

        List<Evidence> assessedEvidence = evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED);

        Map<Pillar, Score> aggregatedScores = new EnumMap<>(Pillar.class);
        boolean hasItaAssessment = false;

        for (Evidence evidence : assessedEvidence) {
            // Check evidence aging rule
            if (evidence.createdDate().isBefore(threeYearsAgo)) {
                continue;
            }

            Optional<Assessment> assessmentOpt = assessmentRepository.findByEvidenceId(evidence.id());
            if (assessmentOpt.isPresent()) {
                Assessment assessment = assessmentOpt.get();

                if (assessment.assessmentDate() != null && assessment.assessmentDate().isBefore(threeYearsAgo)) {
                    continue;
                }

                if (assessment.isThirdParty()) {
                    hasItaAssessment = true;
                }

                // Aggregate highest score for each pillar
                if (assessment.assessedScores() != null) {
                    for (Map.Entry<Pillar, Score> entry : assessment.assessedScores().entrySet()) {
                        Pillar pillar = entry.getKey();
                        Score currentScore = entry.getValue();
                        Score existingScore = aggregatedScores.get(pillar);

                        if (existingScore == null || currentScore.value() > existingScore.value()) {
                            aggregatedScores.put(pillar, currentScore);
                        }
                    }
                }
            }
        }

        CheckInStatus status = evaluateStatus(aggregatedScores, targetGrade, hasItaAssessment);

        CheckIn checkIn = new CheckIn(
                UUID.randomUUID(),
                userId,
                managerId,
                now.minusMonths(3), // Example period start
                now, // Example period end
                aggregatedScores,
                managerNotes,
                status,
                now
        );

        return checkInRepository.save(checkIn);
    }

    private CheckInStatus evaluateStatus(Map<Pillar, Score> aggregatedScores, Grade targetGrade, boolean hasItaAssessment) {
        boolean meetsAllExpectations = true;

        for (Map.Entry<Pillar, Score> entry : targetGrade.expectations().entrySet()) {
            Pillar pillar = entry.getKey();
            Score expected = entry.getValue();
            Score actual = aggregatedScores.get(pillar);

            if (actual == null || actual.value() < expected.value()) {
                meetsAllExpectations = false;
                break;
            }
        }

        if (!meetsAllExpectations) {
            return CheckInStatus.UNDERPERFORMING;
        }

        if (hasItaAssessment) {
            return CheckInStatus.READY_FOR_PROMOTION;
        }

        return CheckInStatus.ON_TRACK;
    }
}
