package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.EvidenceEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.EvidenceJpaRepository;
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import org.acmebank.people.domain.port.EvidenceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaEvidenceRepositoryAdapter implements EvidenceRepository {

    private final EvidenceJpaRepository evidenceJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Evidence save(Evidence evidence) {
        UserEntity userEntity = userJpaRepository.findById(evidence.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + evidence.userId()));

        EvidenceEntity entity = DomainPersistenceMapper.toEvidenceEntity(evidence, userEntity);
        EvidenceEntity saved = evidenceJpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainEvidence(saved);
    }

    @Override
    public Optional<Evidence> findById(UUID id) {
        return evidenceJpaRepository.findById(id)
                .map(DomainPersistenceMapper::toDomainEvidence);
    }

    @Override
    public List<Evidence> findByUserId(UUID userId) {
        return evidenceJpaRepository.findByUserId(userId).stream()
                .map(DomainPersistenceMapper::toDomainEvidence)
                .collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findByUserIdAndStatus(UUID userId, EvidenceStatus status) {
        return evidenceJpaRepository.findByUserIdAndStatus(userId, status.name()).stream()
                .map(DomainPersistenceMapper::toDomainEvidence)
                .collect(Collectors.toList());
    }
}
