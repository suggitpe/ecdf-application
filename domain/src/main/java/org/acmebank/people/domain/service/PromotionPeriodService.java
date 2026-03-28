package org.acmebank.people.domain.service;

import org.acmebank.people.domain.PromotionPeriod;
import org.acmebank.people.domain.PromotionPeriodStatus;
import org.acmebank.people.domain.port.PromotionPeriodRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PromotionPeriodService {

    private final PromotionPeriodRepository repository;

    public PromotionPeriodService(PromotionPeriodRepository repository) {
        this.repository = repository;
    }

    public PromotionPeriod openPeriod(String title, LocalDate start, LocalDate end) {
        if (getActivePeriod().isPresent()) {
            throw new IllegalStateException("Cannot open a new period while another is still OPEN.");
        }

        PromotionPeriod period = new PromotionPeriod(null, title, start, end, PromotionPeriodStatus.OPEN);
        return repository.save(period);
    }

    public PromotionPeriod closePeriod(UUID id) {
        PromotionPeriod period = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion period not found: " + id));

        PromotionPeriod closedPeriod = new PromotionPeriod(
                period.id(),
                period.title(),
                period.startDate(),
                period.endDate(),
                PromotionPeriodStatus.CLOSED
        );
        return repository.save(closedPeriod);
    }

    public Optional<PromotionPeriod> getActivePeriod() {
        return repository.findByStatus(PromotionPeriodStatus.OPEN);
    }

    public List<PromotionPeriod> getAllPeriods() {
        return repository.findAll();
    }
}
