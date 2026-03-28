package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.PromotionCaseEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.*;
import org.acmebank.people.domain.PromotionCase;
import org.acmebank.people.domain.port.PromotionCaseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaPromotionCaseRepositoryAdapter implements PromotionCaseRepository {

    private final PromotionCaseJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final GradeJpaRepository gradeJpaRepository;
    private final PromotionPeriodJpaRepository periodJpaRepository;

    @Override
    public PromotionCase save(PromotionCase promotionCase) {
        PromotionCaseEntity entity = jpaRepository.findById(promotionCase.id() != null ? promotionCase.id() : UUID.randomUUID())
                .orElse(new PromotionCaseEntity());

        var candidate = userJpaRepository.getReferenceById(promotionCase.candidateId());
        var manager = userJpaRepository.getReferenceById(promotionCase.managerId());
        var targetGrade = gradeJpaRepository.getReferenceById(promotionCase.targetGradeId());
        var period = periodJpaRepository.getReferenceById(promotionCase.promotionPeriodId());

        DomainPersistenceMapper.updatePromotionCaseEntity(entity, promotionCase, candidate, manager, targetGrade, period);
        PromotionCaseEntity saved = jpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainPromotionCase(saved);
    }

    @Override
    public Optional<PromotionCase> findById(UUID id) {
        return jpaRepository.findById(id).map(DomainPersistenceMapper::toDomainPromotionCase);
    }

    @Override
    public List<PromotionCase> findByCandidateId(UUID candidateId) {
        return jpaRepository.findByCandidate_Id(candidateId).stream()
                .map(DomainPersistenceMapper::toDomainPromotionCase)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionCase> findByManagerId(UUID managerId) {
        return jpaRepository.findByManager_Id(managerId).stream()
                .map(DomainPersistenceMapper::toDomainPromotionCase)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionCase> findByPromotionPeriodId(UUID promotionPeriodId) {
        return jpaRepository.findByPromotionPeriod_Id(promotionPeriodId).stream()
                .map(DomainPersistenceMapper::toDomainPromotionCase)
                .collect(Collectors.toList());
    }
}
