package org.acmebank.people.domain.port;

import org.acmebank.people.domain.User;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findByManagerId(UUID managerId);

    List<User> findAll();
}
