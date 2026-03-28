package org.acmebank.people.application.config;

import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.EvidenceRating;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.GradeRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import org.acmebank.people.domain.Assessment;
import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.CheckInStatus;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import org.acmebank.people.domain.port.AssessmentRepository;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.EvidenceRepository;

@Configuration
public class DevDataSeeder {

    @Bean
    @Transactional
    public CommandLineRunner seedData(
            UserRepository userRepository, 
            GradeRepository gradeRepository, 
            CheckInRepository checkInRepository,
            EvidenceRepository evidenceRepository,
            AssessmentRepository assessmentRepository,
            org.acmebank.people.domain.port.FrameworkRepository frameworkRepository) {
        return args -> {
            seedFrameworkData(frameworkRepository);
            
            Random random = new Random();
            String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

            // Director expectations for Engineering vs Architecture
            Map<Pillar, Score> dirEngExpectations = new EnumMap<>(Pillar.class);
            Map<Pillar, Score> dirArchExpectations = new EnumMap<>(Pillar.class);
            Map<Pillar, Score> vpExpectations = new EnumMap<>(Pillar.class);
            
            for (Pillar p : Pillar.values()) {
                dirEngExpectations.put(p, new Score(3));
                dirArchExpectations.put(p, new Score(3));
                vpExpectations.put(p, new Score(4));
            }

            // Engineer Director: Delivers 4, Designs 3
            dirEngExpectations.put(Pillar.DELIVERS, new Score(4));
            dirEngExpectations.put(Pillar.DESIGNS, new Score(3));

            // Architect Director: Delivers 3, Designs 4
            dirArchExpectations.put(Pillar.DELIVERS, new Score(3));
            dirArchExpectations.put(Pillar.DESIGNS, new Score(4));

            Grade managementGrade = ensureGrade(gradeRepository, "Director", "Management", dirEngExpectations);
            Grade engineeringDirectorGrade = ensureGrade(gradeRepository, "Director", "Engineering", dirEngExpectations);
            Grade architectureDirectorGrade = ensureGrade(gradeRepository, "Director", "Architecture", dirArchExpectations);
            Grade vpGrade = ensureGrade(gradeRepository, "Vice President", "Engineering", vpExpectations);

            User manager = userRepository.findByEmail("manager@acmebank.org").orElse(null);
            if (manager == null) {
                manager = userRepository.save(new User(null, "manager@acmebank.org", "Manager Mary", managementGrade, null, true, false));
            } else if (manager.grade() == null) {
                manager = userRepository.save(new User(manager.id(), manager.email(), manager.fullName(), managementGrade, manager.managerId(), true, manager.isPromotionCoordinator()));
            }

            // Seed ITAs
            Map<String, String> targetItas = Map.of(
                "ita@acmebank.org", "Assessor Ian",
                "alice@acmebank.org", "Assessor Alice"
            );

            for (Map.Entry<String, String> entry : targetItas.entrySet()) {
                String itaEmail = entry.getKey();
                String itaName = entry.getValue();

                User ita = userRepository.findByEmail(itaEmail).orElse(null);
                if (ita == null) {
                    userRepository.save(new User(null, itaEmail, itaName, managementGrade, null, true, false));
                }
            }

            // Seed Admin
            User admin = userRepository.findByEmail("admin@acmebank.org").orElse(null);
            if (admin == null) {
                userRepository.save(new User(null, "admin@acmebank.org", "System Administrator", managementGrade, null, false, true));
            }

            // Define our target engineers
            Map<String, String> targetEngineers = Map.of(
                "user@acmebank.org", "Developer Dave",
                "charlie@acmebank.org", "Engineer Charlie",
                "bob@acmebank.org", "Engineer Bob",
                "arthur@acmebank.org", "Architect Arthur"
            );

            for (Map.Entry<String, String> entry : targetEngineers.entrySet()) {
                String email = entry.getKey();
                String name = entry.getValue();

                Grade targetGrade = email.equals("arthur@acmebank.org") ? architectureDirectorGrade : engineeringDirectorGrade;

                User engineer = userRepository.findByEmail(email).orElse(null);
                if (engineer == null) {
                    engineer = userRepository.save(new User(null, email, name, targetGrade, manager.id(), false, false));
                } else {
                    boolean needsUpdate = false;
                    Grade currentGrade = engineer.grade();
                    UUID currentManagerId = engineer.managerId();
                    
                    if (currentGrade == null) {
                        currentGrade = targetGrade;
                        needsUpdate = true;
                    }
                    if (currentManagerId == null) {
                        currentManagerId = manager.id();
                        needsUpdate = true;
                    }
                    
                    if (needsUpdate) {
                        engineer = userRepository.save(new User(engineer.id(), engineer.email(), engineer.fullName(), currentGrade, currentManagerId, engineer.isIta(), engineer.isPromotionCoordinator()));
                    }
                }

                // Only seed evidence if they don't have any
                if (evidenceRepository.findByUserId(engineer.id()).isEmpty()) {
                    int count = 4; // Use a fixed count to ensure we cover all pillars
                    for (int i = 0; i < count; i++) {
                        Map<Pillar, EvidenceRating> selfScores = new EnumMap<>(Pillar.class);
                        Map<Pillar, Score> mgrScores = new EnumMap<>(Pillar.class);
                        
                        // Distribute pillars: each evidence covers a subset of pillars
                        // With 4 evidences and 9 pillars, we can do 2-3 pillars per evidence
                        int pillarsPerEvidence = 3;
                        for (int j = 0; j < pillarsPerEvidence; j++) {
                            int pillarIdx = (i * pillarsPerEvidence + j) % Pillar.values().length;
                            Pillar p = Pillar.values()[pillarIdx];
                            int scoreVal = 2 + random.nextInt(3);
                            selfScores.put(p, new EvidenceRating(new Score(scoreVal), "Demonstrated skills for " + p.name()));
                            mgrScores.put(p, new Score(scoreVal));
                        }

                        // Make the first evidence SUBMITTED (Draft state for assessment)
                        EvidenceStatus status = (i == 0) ? EvidenceStatus.SUBMITTED : EvidenceStatus.MANAGER_ASSESSED;

                        int monthsAgo = 3 + (i * 5); // 3, 8, 13, 18 months ago -> ranges up to ~2 years, all at least 3 months

                        Evidence evidence = evidenceRepository.save(new Evidence(
                            null, engineer.id(), 
                            "Project " + (char)('A' + i + 1) + " Implementation",
                            loremIpsum, loremIpsum, loremIpsum, loremIpsum,
                            selfScores, Collections.emptyList(), Collections.emptyList(),
                            status,
                            LocalDate.now().minusMonths(monthsAgo), LocalDate.now().minusMonths(monthsAgo)
                        ));

                        // Only save assessment if status is MANAGER_ASSESSED
                        if (status == EvidenceStatus.MANAGER_ASSESSED) {
                            Map<Pillar, EvidenceRating> managerAssessmentRatings = mgrScores.entrySet().stream()
                                .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> new EvidenceRating(e.getValue(), "Manager assessed " + e.getKey().name()),
                                    (oldValue, newValue) -> oldValue, // Merge function for duplicates, though not expected here
                                    () -> new EnumMap<>(Pillar.class)
                                ));
                            assessmentRepository.save(new Assessment(
                                null, evidence.id(), manager.id(),
                                managerAssessmentRatings, "Assessed based on project outcomes.",
                                false, LocalDate.now().minusMonths(monthsAgo)
                            ));
                        }
                    }
                }

                // Seed Check-Ins to show a history over the same two year period
                if (checkInRepository.findByUserId(engineer.id()).isEmpty()) {
                    List<Evidence> userEvidence = evidenceRepository.findByUserId(engineer.id());
                    UUID latestEvidenceId = userEvidence.isEmpty() ? null : userEvidence.get(userEvidence.size() - 1).id();
                    
                    Map<Pillar, org.acmebank.people.domain.PillarScoreInfo> seededScores = vpExpectations.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> new org.acmebank.people.domain.PillarScoreInfo(e.getValue(), latestEvidenceId)));

                    for (int i = 0; i < 4; i++) { // Generate 4 checkins
                        int monthsAgo = 3 + (i * 6); // 3, 9, 15, 21 months ago
                        
                        CheckInStatus status = email.equals("charlie@acmebank.org") ? CheckInStatus.READY_FOR_PROMOTION : 
                                             (email.equals("user@acmebank.org") ? CheckInStatus.UNDERPERFORMING : CheckInStatus.ON_TRACK);
                        
                        checkInRepository.save(new CheckIn(null, engineer.id(), manager.id(), 
                            seededScores, "Quarterly review summary for " + engineer.fullName(), 
                            status, LocalDate.now().minusMonths(monthsAgo))); // check-in date
                    }

                    // Seed one DRAFT check-in for user@example.com
                    if (email.equals("user@acmebank.org")) {
                        checkInRepository.save(new CheckIn(null, engineer.id(), manager.id(),
                            seededScores, "This is a draft check-in notes.",
                            CheckInStatus.DRAFT, LocalDate.now().minusDays(1)));
                    }
                }
            }

            // Seed evidence for the manager persona
            if (evidenceRepository.findByUserId(manager.id()).isEmpty()) {
                for (int i = 0; i < 2; i++) {
                    Map<Pillar, EvidenceRating> selfScores = new EnumMap<>(Pillar.class);
                    Map<Pillar, Score> mgrScores = new EnumMap<>(Pillar.class);

                    // Assign some pillars for manager evidence (covering different pillars than engineers)
                    int startPillar = i * 4;
                    for (int j = 0; j < 3; j++) {
                        Pillar p = Pillar.values()[(startPillar + j) % Pillar.values().length];
                        int scoreVal = 4 + random.nextInt(2); // Managers usually have higher scores (4-5)
                        selfScores.put(p, new EvidenceRating(new Score(scoreVal), "Excellent operational leadership on " + p.name()));
                        mgrScores.put(p, new Score(scoreVal));
                    }
                    
                    int monthsAgo = 3 + (i * 12); // 3, 15 months ago

                    Evidence evidence = evidenceRepository.save(new Evidence(
                        null, manager.id(),
                        "Strategic Initiative: " + (i == 0 ? "Global Engineering Standardisation" : "Management Training Framework"),
                        loremIpsum, loremIpsum, loremIpsum, loremIpsum,
                        selfScores, Collections.emptyList(), Collections.emptyList(),
                        EvidenceStatus.MANAGER_ASSESSED,
                        LocalDate.now().minusMonths(monthsAgo), LocalDate.now().minusMonths(monthsAgo)
                    ));

                    Map<Pillar, EvidenceRating> managerAssessmentRatings = mgrScores.entrySet().stream()
                        .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new EvidenceRating(e.getValue(), "Manager assessed " + e.getKey().name()),
                            (oldValue, newValue) -> oldValue,
                            () -> new EnumMap<>(Pillar.class)
                        ));

                    assessmentRepository.save(new Assessment(
                        null, evidence.id(), manager.id(), // Assessed by self or a peer for example purposes
                        managerAssessmentRatings, "Excellent strategic impact and leadership.",
                        false, LocalDate.now().minusMonths(monthsAgo)
                    ));
                }
            }

            // Assign some evidence to ITA for review
            User ian = userRepository.findByEmail("ita@acmebank.org").orElse(null);
            User charlieUser = userRepository.findByEmail("charlie@acmebank.org").orElse(null);
            
            if (ian != null && charlieUser != null) {
                // Find Charlie's first evidence that isn't already being assessed by him
                List<Evidence> charlieEvidence = evidenceRepository.findByUserId(charlieUser.id());
                if (!charlieEvidence.isEmpty()) {
                    Evidence toAssign = charlieEvidence.get(0);
                    
                    // Only assign if not already assigned
                    if (assessmentRepository.findByEvidenceId(toAssign.id()).stream().noneMatch(Assessment::isThirdParty)) {
                        Evidence assigned = new Evidence(
                            toAssign.id(), toAssign.userId(), toAssign.title(),
                            toAssign.description(), toAssign.impact(), toAssign.complexity(), toAssign.contribution(),
                            toAssign.selfAssessment(), toAssign.links(), toAssign.attachmentPaths(),
                            EvidenceStatus.UNDER_INDEPENDENT_REVIEW,
                            toAssign.createdDate(), LocalDate.now()
                        );
                        evidenceRepository.save(assigned);
                        
                        // For ITA assessment, let's use some arbitrary scores
                        int baseline = 3; // Example baseline score
                        Map<Pillar, EvidenceRating> itaScores = Map.of(
                                Pillar.THINKS, new EvidenceRating(new Score(baseline), "ITA logic for THINKS"),
                                Pillar.ENGAGES, new EvidenceRating(new Score(baseline + 1), "ITA logic for ENGAGES"),
                                Pillar.DELIVERS, new EvidenceRating(new Score(baseline), "ITA logic for DELIVERS")
                        );
                        assessmentRepository.save(new Assessment(
                            null, assigned.id(), ian.id(), itaScores,
                            "ITA review: Technically sound and well executed.",
                            true, LocalDate.now()
                        ));
                    }
                }
            }
        };
    }

    private void seedPromotionPeriod(org.acmebank.people.domain.port.PromotionPeriodRepository repository) {
        if (repository.findByStatus(org.acmebank.people.domain.PromotionPeriodStatus.OPEN).isEmpty()) {
            repository.save(new org.acmebank.people.domain.PromotionPeriod(
                null,
                "Q1 2026 Promotion Cycle",
                LocalDate.now().minusDays(7),
                LocalDate.now().plusMonths(1),
                org.acmebank.people.domain.PromotionPeriodStatus.OPEN
            ));
        }
    }

    private Grade ensureGrade(GradeRepository repository, String name, String role, Map<Pillar, Score> expectations) {
        return repository.findByNameAndRole(name, role)
                .map(existing -> {
                    if (!existing.expectations().equals(expectations)) {
                        return repository.save(new Grade(existing.id(), name, role, expectations));
                    }
                    return existing;
                })
                .orElseGet(() -> repository.save(new Grade(null, name, role, expectations)));
    }

    private void seedFrameworkData(org.acmebank.people.domain.port.FrameworkRepository frameworkRepository) {
        if (frameworkRepository.findAllPillars().isEmpty()) {
            for (Pillar p : Pillar.values()) {
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

                List<org.acmebank.people.domain.FrameworkLevel> levels = new java.util.ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    String levelDesc = switch (i) {
                        case 1 -> "Novice: Focuses on following rules and standard procedures. Requires high levels of supervision.";
                        case 2 -> "Advanced Beginner: Can perform simple tasks independently but still needs help for complex ones.";
                        case 3 -> "Competent: Can handle common non-routine tasks and solve problems through analytical reasoning.";
                        case 4 -> "Proficient: Sees situations holistically and identifies important issues intuitively based on experience.";
                        case 5 -> "Expert: Has deep intuitive grasp and can innovate or push industry boundaries.";
                        default -> "";
                    };
                    
                    String examples = "Completed assigned tasks correctly and on time\nParticipated in code reviews highlighting best practices";
                    if (i >= 4) {
                        examples = "Led a cross-team initiative to standardized data patterns\nMentored multiple engineers to achieve their promotion goals";
                    }

                    levels.add(new org.acmebank.people.domain.FrameworkLevel(null, p, new Score(i), levelDesc, examples));
                }

                frameworkRepository.savePillar(new org.acmebank.people.domain.FrameworkPillar(p, title, description, levels));
            }
        }
    }
}
