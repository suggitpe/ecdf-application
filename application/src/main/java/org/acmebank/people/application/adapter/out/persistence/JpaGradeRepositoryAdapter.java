package org.acmebank.people.application.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity;
import org.acmebank.people.application.adapter.out.persistence.mapper.DomainPersistenceMapper;
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository;
import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.port.GradeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaGradeRepositoryAdapter implements GradeRepository {

    private final GradeJpaRepository gradeJpaRepository;

    @Override
    public Grade save(Grade grade) {
        GradeEntity entity = DomainPersistenceMapper.toGradeEntity(grade);
        GradeEntity saved = gradeJpaRepository.save(entity);
        return DomainPersistenceMapper.toDomainGrade(saved);
    }

    @Override
    public Optional<Grade> findById(UUID id) {
        return gradeJpaRepository.findById(id)
                .map(DomainPersistenceMapper::toDomainGrade);
    }

    @Override
    public Optional<Grade> findByNameAndRole(String name, String role) {
        return gradeJpaRepository.findByNameAndRole(name, role)
                .map(DomainPersistenceMapper::toDomainGrade);
    }

    @Override
    public List<Grade> findAll() {
        return gradeJpaRepository.findAll().stream()
                .map(DomainPersistenceMapper::toDomainGrade)
                .collect(Collectors.toList());
    }
}
