package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.PromotionPeriodEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.PromotionPeriodJpaRepository;
import org.acmebank.people.domain.PromotionPeriod;
import org.acmebank.people.domain.PromotionPeriodStatus;
import org.acmebank.people.domain.port.PromotionPeriodRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaPromotionPeriodRepositoryAdapter implements PromotionPeriodRepository {

    private final PromotionPeriodJpaRepository jpaRepository;

    @Override
    public PromotionPeriod save(PromotionPeriod promotionPeriod) {
        PromotionPeriodEntity entity = jpaRepository.findById(promotionPeriod.id() != null ? promotionPeriod.id() : UUID.randomUUID())
                .orElse(new PromotionPeriodEntity());
        DomainPersistenceMapper.updatePromotionPeriodEntity(entity, promotionPeriod);
        PromotionPeriodEntity saved = jpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainPromotionPeriod(saved);
    }

    @Override
    public Optional<PromotionPeriod> findByStatus(PromotionPeriodStatus status) {
        return jpaRepository.findByStatus(status).map(DomainPersistenceMapper::toDomainPromotionPeriod);
    }

    @Override
    public Optional<PromotionPeriod> findById(UUID id) {
        return jpaRepository.findById(id).map(DomainPersistenceMapper::toDomainPromotionPeriod);
    }

    @Override
    public List<PromotionPeriod> findAll() {
        return jpaRepository.findAll().stream()
                .map(DomainPersistenceMapper::toDomainPromotionPeriod)
                .collect(Collectors.toList());
    }
}
