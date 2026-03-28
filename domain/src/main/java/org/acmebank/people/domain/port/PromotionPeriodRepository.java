package org.acmebank.people.domain.port;

import org.acmebank.people.domain.PromotionPeriod;
import org.acmebank.people.domain.PromotionPeriodStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PromotionPeriodRepository {
    PromotionPeriod save(PromotionPeriod promotionPeriod);
    Optional<PromotionPeriod> findByStatus(PromotionPeriodStatus status);
    Optional<PromotionPeriod> findById(UUID id);
    List<PromotionPeriod> findAll();
}
