package org.acmebank.people.application.adapter.out.persistence.repository;

import org.acmebank.people.application.adapter.out.persistence.entity.PdpItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PdpItemJpaRepository extends JpaRepository<PdpItemEntity, UUID> {
    List<PdpItemEntity> findByUserId(UUID userId);

    List<PdpItemEntity> findByCheckInId(UUID checkInId);
}
