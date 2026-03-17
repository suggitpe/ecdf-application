package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository;
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final GradeJpaRepository gradeJpaRepository;

    @Override
    public User save(User user) {
        GradeEntity gradeEntity = null;
        if (user.grade() != null) {
            gradeEntity = gradeJpaRepository.findById(user.grade().id()).orElse(null);
        }

        UserEntity managerEntity = null;
        if (user.managerId() != null) {
            managerEntity = userJpaRepository.findById(user.managerId()).orElse(null);
        }

        UserEntity entity = DomainPersistenceMapper.toUserEntity(user, gradeEntity, managerEntity);
        UserEntity saved = userJpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainUser(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(DomainPersistenceMapper::toDomainUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(DomainPersistenceMapper::toDomainUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByManagerId(UUID managerId) {
        return userJpaRepository.findByManagerId(managerId).stream()
                .map(DomainPersistenceMapper::toDomainUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findItas() {
        return userJpaRepository.findByIsItaTrue().stream()
                .map(DomainPersistenceMapper::toDomainUser)
                .collect(Collectors.toList());
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(DomainPersistenceMapper::toDomainUser)
                .collect(Collectors.toList());
    }
}
