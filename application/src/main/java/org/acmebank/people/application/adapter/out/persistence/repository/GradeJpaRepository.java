package org.acmebank.people.application.adapter.out.persistence.repository;

import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeJpaRepository extends JpaRepository<GradeEntity, UUID> {
    Optional<GradeEntity> findByNameAndRole(String name, String role);
}
