package org.acmebank.people.application.adapter.out.framework;

import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.PillarDefinition;
import org.acmebank.people.domain.PillarLevelDetail;
import org.acmebank.people.domain.port.PillarFrameworkService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StaticPillarFrameworkService implements PillarFrameworkService {

    private final Map<Pillar, PillarDefinition> definitions = new EnumMap<>(Pillar.class);

    public StaticPillarFrameworkService() {
        for (Pillar p : Pillar.values()) {
            definitions.put(p, createDefinition(p));
        }
    }

    @Override
    public List<PillarDefinition> getAllDefinitions() {
        return new ArrayList<>(definitions.values());
    }

    @Override
    public Optional<PillarDefinition> getDefinition(Pillar pillar) {
        return Optional.ofNullable(definitions.get(pillar));
    }

    private PillarDefinition createDefinition(Pillar p) {
        String title = switch (p) {
            case THINKS -> "Analytical & Strategic Thinking";
            case ENGAGES -> "Collaboration & Growth";
            case INFLUENCES -> "Impact & Alignment";
            case ACHIEVES -> "Reliability & Result Orientation";
            case DESIGNS -> "Strategic Design & Architecture";
            case DELIVERS -> "Execution & Engineering Standard";
            case CONTROLS -> "Safety, Security & Risk";
            case OPERATES -> "Sustainability & Observability";
        };

        String description = switch (p) {
            case THINKS -> "Critical and analytical reasoning to solve complex engineering problems.";
            case ENGAGES -> "Collaborating with peers and stakeholders to drive collective improvement.";
            case INFLUENCES -> "Shaping directions and outcomes beyond your immediate squad.";
            case ACHIEVES -> "Taking ownership and delivering high-quality results consistently.";
            case DESIGNS -> "Creating robust, scalable and decoupled technical architectures.";
            case DELIVERS -> "Implementing and shipping high-quality code into production safely.";
            case CONTROLS -> "Ensuring our systems are secure, compliant and risk-aware.";
            case OPERATES -> "Owning the performance, health and maintenance of systems.";
        };

        List<PillarLevelDetail> levels = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            levels.add(new PillarLevelDetail(i, getLevelDescription(i), getExamples(p, i)));
        }

        return new PillarDefinition(p, title, description, levels);
    }

    private String getLevelDescription(int level) {
        return switch (level) {
            case 1 -> "Novice: Focuses on following rules and standard procedures. Requires high levels of supervision.";
            case 2 -> "Advanced Beginner: Can perform simple tasks independently but still needs help for complex ones.";
            case 3 -> "Competent: Can handle common non-routine tasks and solve problems through analytical reasoning.";
            case 4 -> "Proficient: Sees situations holistically and identifies important issues intuitively based on experience.";
            case 5 -> "Expert: Has deep intuitive grasp and can innovate or push industry boundaries.";
            default -> "";
        };
    }

    private List<String> getExamples(Pillar p, int level) {
        List<String> examples = new ArrayList<>();
        if (level == 3) {
            examples.add("Authored a technical specification for a new component");
            examples.add("Conducted a deep-dive analysis into a performance bottleneck");
        } else if (level == 4) {
            examples.add("Led a cross-team initiative to standardized data patterns");
            examples.add("Mentored multiple engineers to achieve their promotion goals");
        } else {
            examples.add("Completed assigned tasks correctly and on time");
            examples.add("Participated in code reviews highlighting best practices");
        }
        return examples;
    }
}
