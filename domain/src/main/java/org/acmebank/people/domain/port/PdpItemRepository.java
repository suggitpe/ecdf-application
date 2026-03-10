package org.acmebank.people.domain.port;

import org.acmebank.people.domain.PdpItem;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PdpItemRepository {
    PdpItem save(PdpItem pdpItem);

    Optional<PdpItem> findById(UUID id);

    List<PdpItem> findByUserId(UUID userId);

    List<PdpItem> findByCheckInId(UUID checkInId);
}
