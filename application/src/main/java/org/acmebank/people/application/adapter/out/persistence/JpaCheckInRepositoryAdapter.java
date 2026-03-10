package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.CheckInEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.CheckInJpaRepository;
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository;
import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.port.CheckInRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaCheckInRepositoryAdapter implements CheckInRepository {

    private final CheckInJpaRepository checkInJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public CheckIn save(CheckIn checkIn) {
        UserEntity userEntity = userJpaRepository.findById(checkIn.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + checkIn.userId()));

        UserEntity managerEntity = userJpaRepository.findById(checkIn.managerId())
                .orElseThrow(() -> new IllegalArgumentException("Manager not found: " + checkIn.managerId()));

        CheckInEntity entity = DomainPersistenceMapper.toCheckInEntity(checkIn, userEntity, managerEntity);
        CheckInEntity saved = checkInJpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainCheckIn(saved);
    }

    @Override
    public Optional<CheckIn> findById(UUID id) {
        return checkInJpaRepository.findById(id)
                .map(DomainPersistenceMapper::toDomainCheckIn);
    }

    @Override
    public List<CheckIn> findByUserId(UUID userId) {
        return checkInJpaRepository.findByUserId(userId).stream()
                .map(DomainPersistenceMapper::toDomainCheckIn)
                .collect(Collectors.toList());
    }
}
