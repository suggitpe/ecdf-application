package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository;
import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.port.GradeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class JpaGradeRepositoryAdapter implements GradeRepository {

    private final GradeJpaRepository gradeJpaRepository;

    @Override
    public Grade save(Grade grade) {
        GradeEntity entity = DomainPersistenceMapper.toGradeEntity(grade);
        GradeEntity saved = gradeJpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainGrade(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Grade> findById(UUID id) {
        if (id == null) return Optional.empty();
        return gradeJpaRepository.findById(id)
                .map(DomainPersistenceMapper::toDomainGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Grade> findByNameAndRole(String name, String role) {
        return gradeJpaRepository.findByNameAndRole(name, role)
                .map(DomainPersistenceMapper::toDomainGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findAll() {
        return gradeJpaRepository.findAll().stream()
                .map(DomainPersistenceMapper::toDomainGrade)
                .collect(Collectors.toList());
    }

    @Override
    public void updateExpectations(UUID gradeId, java.util.Map<org.acmebank.people.domain.Pillar, org.acmebank.people.domain.Score> expectations) {
        gradeJpaRepository.findById(gradeId).ifPresent(entity -> {
            // Clear existing and add new
            entity.getExpectations().clear();
            expectations.forEach((pillar, score) -> {
                org.acmebank.people.application.adapter.out.persistence.entity.GradeExpectationEntity expectation = new org.acmebank.people.application.adapter.out.persistence.entity.GradeExpectationEntity();
                expectation.setId(new org.acmebank.people.application.adapter.out.persistence.entity.GradeExpectationId(gradeId, pillar.name()));
                expectation.setExpectedScore(score.value());
                expectation.setGrade(entity);
                entity.getExpectations().add(expectation);
            });
            gradeJpaRepository.save(entity);
        });
    }
}
