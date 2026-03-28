package org.acmebank.people.application.adapter.out.persistence.repository;

import org.acmebank.people.application.adapter.out.persistence.entity.PromotionCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PromotionCaseJpaRepository extends JpaRepository<PromotionCaseEntity, UUID> {
    List<PromotionCaseEntity> findByCandidate_Id(UUID candidateId);
    List<PromotionCaseEntity> findByManager_Id(UUID managerId);
    List<PromotionCaseEntity> findByPromotionPeriod_Id(UUID promotionPeriodId);
}
