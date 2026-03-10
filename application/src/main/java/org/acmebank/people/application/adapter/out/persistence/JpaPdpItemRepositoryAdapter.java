package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.CheckInEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.PdpItemEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.CheckInJpaRepository;
import org.acmebank.people.application.adapter.out.persistence.repository.PdpItemJpaRepository;
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository;
import org.acmebank.people.domain.PdpItem;
import org.acmebank.people.domain.port.PdpItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaPdpItemRepositoryAdapter implements PdpItemRepository {

    private final PdpItemJpaRepository pdpItemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CheckInJpaRepository checkInJpaRepository;

    @Override
    public PdpItem save(PdpItem pdpItem) {
        UserEntity userEntity = userJpaRepository.findById(pdpItem.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + pdpItem.userId()));

        CheckInEntity checkInEntity = checkInJpaRepository.findById(pdpItem.checkInId())
                .orElseThrow(() -> new IllegalArgumentException("CheckIn not found: " + pdpItem.checkInId()));

        PdpItemEntity entity = DomainPersistenceMapper.toPdpItemEntity(pdpItem, userEntity, checkInEntity);
        PdpItemEntity saved = pdpItemJpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainPdpItem(saved);
    }

    @Override
    public Optional<PdpItem> findById(UUID id) {
        return pdpItemJpaRepository.findById(id)
                .map(DomainPersistenceMapper::toDomainPdpItem);
    }

    @Override
    public List<PdpItem> findByUserId(UUID userId) {
        return pdpItemJpaRepository.findByUserId(userId).stream()
                .map(DomainPersistenceMapper::toDomainPdpItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<PdpItem> findByCheckInId(UUID checkInId) {
        return pdpItemJpaRepository.findByCheckInId(checkInId).stream()
                .map(DomainPersistenceMapper::toDomainPdpItem)
                .collect(Collectors.toList());
    }
}
