package org.acmebank.people.application.adapter.out.persistence;

import org.acmebank.people.application.adapter.out.persistence.entity.FrameworkLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataFrameworkLevelRepository extends JpaRepository<FrameworkLevelEntity, UUID> {
}
