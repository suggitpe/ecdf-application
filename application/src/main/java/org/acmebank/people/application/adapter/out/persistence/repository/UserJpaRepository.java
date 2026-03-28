package org.acmebank.people.application.adapter.out.persistence.repository;

import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByManagerId(UUID managerId);
 
    List<UserEntity> findByItaTrue();
}
