package org.acmebank.people.domain.port;

import org.acmebank.people.domain.PromotionCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromotionCaseRepository {
    PromotionCase save(PromotionCase promotionCase);
    Optional<PromotionCase> findById(UUID id);
    List<PromotionCase> findByCandidateId(UUID candidateId);
    List<PromotionCase> findByManagerId(UUID managerId);
    List<PromotionCase> findByPromotionPeriodId(UUID promotionPeriodId);
}
