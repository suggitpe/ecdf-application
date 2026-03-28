package org.acmebank.people.domain.service;

import org.acmebank.people.domain.*;
import org.acmebank.people.domain.port.*;

import java.util.List;
import java.util.UUID;

public class PromotionService {

    private final PromotionCaseRepository promotionCaseRepository;
    private final PromotionPeriodRepository promotionPeriodRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    public PromotionService(PromotionCaseRepository promotionCaseRepository,
                            PromotionPeriodRepository promotionPeriodRepository,
                            UserRepository userRepository,
                            GradeRepository gradeRepository) {
        this.promotionCaseRepository = promotionCaseRepository;
        this.promotionPeriodRepository = promotionPeriodRepository;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

    public PromotionCase proposeCandidate(UUID candidateId, UUID managerId, UUID targetGradeId, String rationale) {
        PromotionPeriod activePeriod = promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)
                .orElseThrow(() -> new IllegalStateException("Promotion proposals can only be submitted during an OPEN promotion period."));

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + candidateId));

        if (candidate.managerId() == null || !candidate.managerId().equals(managerId)) {
            throw new IllegalArgumentException("Manager is not authorized to propose this candidate (not a direct report).");
        }

        gradeRepository.findById(targetGradeId)
                .orElseThrow(() -> new IllegalArgumentException("Target grade not found: " + targetGradeId));

        PromotionCase promotionCase = new PromotionCase(
                null,
                candidateId,
                managerId,
                targetGradeId,
                activePeriod.id(),
                rationale,
                PromotionStatus.PROPOSED
        );

        return promotionCaseRepository.save(promotionCase);
    }

    public List<PromotionCase> getCasesForPeriod(UUID periodId) {
        return promotionCaseRepository.findByPromotionPeriodId(periodId);
    }

    public List<PromotionCase> getActiveCases() {
        return promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)
                .map(period -> promotionCaseRepository.findByPromotionPeriodId(period.id()))
                .orElse(List.of());
    }
}
