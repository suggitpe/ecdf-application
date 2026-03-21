package org.acmebank.people.application.adapter.out.persistence;

import org.acmebank.people.application.adapter.out.persistence.entity.FrameworkPillarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataFrameworkPillarRepository extends JpaRepository<FrameworkPillarEntity, String> {
}
