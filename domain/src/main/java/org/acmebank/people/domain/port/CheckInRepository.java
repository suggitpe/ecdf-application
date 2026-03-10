package org.acmebank.people.domain.port;

import org.acmebank.people.domain.CheckIn;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface CheckInRepository {
    CheckIn save(CheckIn checkIn);

    Optional<CheckIn> findById(UUID id);

    List<CheckIn> findByUserId(UUID userId);
}
