package org.acmebank.people.application.adapter.out.persistence.repository;

import org.acmebank.people.application.adapter.out.persistence.entity.PromotionPeriodEntity;
import org.acmebank.people.domain.PromotionPeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionPeriodJpaRepository extends JpaRepository<PromotionPeriodEntity, UUID> {
    Optional<PromotionPeriodEntity> findByStatus(PromotionPeriodStatus status);
}
