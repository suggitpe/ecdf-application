package org.acmebank.people.application.adapter.out.persistence.repository;

import org.acmebank.people.application.adapter.out.persistence.entity.EvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvidenceJpaRepository extends JpaRepository<EvidenceEntity, UUID> {
    List<EvidenceEntity> findByUserId(UUID userId);

    List<EvidenceEntity> findByUserIdAndStatus(UUID userId, String status);
}
