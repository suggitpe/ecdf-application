package org.acmebank.people.application.adapter.out.persistence;

import org.acmebank.people.application.adapter.out.persistence.entity.FrameworkLevelEntity;
import org.acmebank.people.application.adapter.out.persistence.entity.FrameworkPillarEntity;
import org.acmebank.people.domain.FrameworkLevel;
import org.acmebank.people.domain.FrameworkPillar;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.port.FrameworkRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaFrameworkRepositoryAdapter implements FrameworkRepository {

    private final SpringDataFrameworkPillarRepository pillarRepository;

    public JpaFrameworkRepositoryAdapter(SpringDataFrameworkPillarRepository pillarRepository) {
        this.pillarRepository = pillarRepository;
    }

    @Override
    public List<FrameworkPillar> findAllPillars() {
        return pillarRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FrameworkPillar> findPillar(Pillar pillar) {
        return pillarRepository.findById(pillar.name())
                .map(this::toDomain);
    }

    @Override
    public FrameworkPillar savePillar(FrameworkPillar pillar) {
        FrameworkPillarEntity entity = new FrameworkPillarEntity();
        entity.setPillarName(pillar.pillar().name());
        entity.setTitle(pillar.title());
        entity.setDescription(pillar.description());

        for (FrameworkLevel level : pillar.levels()) {
            FrameworkLevelEntity levelEntity = new FrameworkLevelEntity();
            levelEntity.setId(level.id());
            levelEntity.setPillar(entity);
            levelEntity.setScore(level.score().value());
            levelEntity.setLevelDescription(level.levelDescription());
            levelEntity.setEvidenceExamples(level.evidenceExamples());
            entity.getLevels().add(levelEntity);
        }

        FrameworkPillarEntity saved = pillarRepository.save(entity);
        return toDomain(saved);
    }

    private FrameworkPillar toDomain(FrameworkPillarEntity entity) {
        List<FrameworkLevel> levels = entity.getLevels().stream()
                .map(le -> new FrameworkLevel(
                        le.getId(),
                        Pillar.valueOf(entity.getPillarName()),
                        new Score(le.getScore()),
                        le.getLevelDescription(),
                        le.getEvidenceExamples()
                ))
                .collect(Collectors.toList());

        return new FrameworkPillar(
                Pillar.valueOf(entity.getPillarName()),
                entity.getTitle(),
                entity.getDescription(),
                levels
        );
    }
}
