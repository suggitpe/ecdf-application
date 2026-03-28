package org.acmebank.people.application.adapter.out.framework;

import org.acmebank.people.domain.FrameworkLevel;
import org.acmebank.people.domain.FrameworkPillar;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.PillarDefinition;
import org.acmebank.people.domain.PillarLevelDetail;
import org.acmebank.people.domain.port.FrameworkRepository;
import org.acmebank.people.domain.port.PillarFrameworkService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary
public class DatabasePillarFrameworkService implements PillarFrameworkService {

    private final FrameworkRepository frameworkRepository;

    public DatabasePillarFrameworkService(FrameworkRepository frameworkRepository) {
        this.frameworkRepository = frameworkRepository;
    }

    @Override
    public List<PillarDefinition> getAllDefinitions() {
        return frameworkRepository.findAllPillars().stream()
                .map(this::toPillarDefinition)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PillarDefinition> getDefinition(Pillar pillar) {
        return frameworkRepository.findPillar(pillar)
                .map(this::toPillarDefinition);
    }

    private PillarDefinition toPillarDefinition(org.acmebank.people.domain.FrameworkPillar frameworkPillar) {
        List<PillarLevelDetail> levels = frameworkPillar.levels().stream()
                .map(this::toLevelDetail)
                .collect(Collectors.toList());

        return new PillarDefinition(
                frameworkPillar.pillar(),
                frameworkPillar.title(),
                frameworkPillar.description(),
                levels
        );
    }

    private PillarLevelDetail toLevelDetail(FrameworkLevel level) {
        List<String> examples = Arrays.asList(level.evidenceExamples().split("\n"));
        return new PillarLevelDetail(
                level.score().value(),
                level.levelDescription(),
                examples
        );
    }

    @Override
    public void updateDefinition(PillarDefinition definition) {
        frameworkRepository.findPillar(definition.pillar()).ifPresent(existing -> {
            FrameworkPillar updated = new FrameworkPillar(
                    existing.pillar(),
                    definition.title(),
                    definition.description(),
                    existing.levels()
            );
            frameworkRepository.savePillar(updated);
        });
    }
}
