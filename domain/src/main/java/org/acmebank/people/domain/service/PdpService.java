package org.acmebank.people.domain.service;

import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.PdpItem;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.port.PdpItemRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PdpService {

    private final PdpItemRepository pdpItemRepository;

    public PdpService(PdpItemRepository pdpItemRepository) {
        this.pdpItemRepository = pdpItemRepository;
    }

    public PdpItem createPdpItem(UUID userId, UUID checkInId, Pillar pillar, String gapDescription, String actionablePlan, String learningJourneyLink) {
        PdpItem item = new PdpItem(
                null,
                userId,
                checkInId,
                pillar,
                gapDescription,
                actionablePlan,
                learningJourneyLink,
                false,
                LocalDate.now(),
                LocalDate.now()
        );
        return pdpItemRepository.save(item);
    }

    public List<PdpItem> autoGenerateMandatoryPdps(CheckIn checkIn, Grade currentGrade) {
        List<PdpItem> generatedItems = new ArrayList<>();

        for (Map.Entry<Pillar, Score> entry : currentGrade.expectations().entrySet()) {
            Pillar pillar = entry.getKey();
            Score expected = entry.getValue();
            org.acmebank.people.domain.PillarScoreInfo actualInfo = checkIn.holisticScores().get(pillar);
            Score actual = actualInfo != null ? actualInfo.score() : null;

            if (actual == null || actual.value() < expected.value()) {
                int actualValue = (actual == null) ? 0 : actual.value();
                String gapDesc = String.format("Below expectation for %s. Expected: %d, Actual: %d", pillar.name(), expected.value(), actualValue);
                
                PdpItem item = createPdpItem(
                        checkIn.userId(),
                        checkIn.id(),
                        pillar,
                        gapDesc,
                        "To be discussed with manager to formulate an actionable plan.",
                        "https://learning.acmebank.com/search?q=" + pillar.name()
                );
                generatedItems.add(item);
            }
        }

        return generatedItems;
    }

    public PdpItem markAsCompleted(UUID pdpItemId) {
        Optional<PdpItem> optItem = pdpItemRepository.findById(pdpItemId);
        if (optItem.isPresent()) {
            PdpItem item = optItem.get();
            PdpItem updated = new PdpItem(
                    item.id(),
                    item.userId(),
                    item.checkInId(),
                    item.targetedPillar(),
                    item.gapDescription(),
                    item.actionablePlan(),
                    item.learningJourneyLink(),
                    true,
                    item.createdDate(),
                    LocalDate.now()
            );
            return pdpItemRepository.save(updated);
        }
        throw new IllegalArgumentException("PDP Item not found");
    }
}
