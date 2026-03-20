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

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
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
            AssessmentRepository assessmentRepository) {
        return args -> {
            Random random = new Random();
            String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

            // Define expectations (demonstrating individual pillar levels)
            Map<Pillar, Score> vpExpectations = new EnumMap<>(Pillar.class);
            Map<Pillar, Score> directorExpectations = new EnumMap<>(Pillar.class);
            
            // Default baseline scores
            for (Pillar p : Pillar.values()) {
                vpExpectations.put(p, new Score(3));
                directorExpectations.put(p, new Score(4));
            }

            // Example of setting individual pillar levels (e.g. higher expectations for some technical/behavioral aspects)
            // vpExpectations.put(Pillar.DELIVERS, new Score(4)); 
            // directorExpectations.put(Pillar.INFLUENCES, new Score(5));

            Grade managerGrade = ensureGrade(gradeRepository, "Director", "Management", directorExpectations);
            Grade engineerGrade = ensureGrade(gradeRepository, "Vice President", "Engineering", vpExpectations);

            User manager = userRepository.findByEmail("manager@example.com").orElse(null);
            if (manager == null) {
                manager = userRepository.save(new User(null, "manager@example.com", "Manager Mary", managerGrade, null, true));
            } else if (manager.grade() == null) {
                manager = userRepository.save(new User(manager.id(), manager.email(), manager.fullName(), managerGrade, manager.managerId(), manager.isIta()));
            }

            // Define our target engineers
            Map<String, String> targetEngineers = Map.of(
                "user@example.com", "Developer Dave",
                "charlie@example.com", "Engineer Charlie",
                "bob@example.com", "Engineer Bob"
            );

            for (Map.Entry<String, String> entry : targetEngineers.entrySet()) {
                String email = entry.getKey();
                String name = entry.getValue();

                User engineer = userRepository.findByEmail(email).orElse(null);
                if (engineer == null) {
                    engineer = userRepository.save(new User(null, email, name, engineerGrade, manager.id(), false));
                } else {
                    boolean needsUpdate = false;
                    Grade currentGrade = engineer.grade();
                    UUID currentManagerId = engineer.managerId();
                    
                    if (currentGrade == null) {
                        currentGrade = engineerGrade;
                        needsUpdate = true;
                    }
                    if (currentManagerId == null) {
                        currentManagerId = manager.id();
                        needsUpdate = true;
                    }
                    
                    if (needsUpdate) {
                        engineer = userRepository.save(new User(engineer.id(), engineer.email(), engineer.fullName(), currentGrade, currentManagerId, engineer.isIta()));
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
                            assessmentRepository.save(new Assessment(
                                null, evidence.id(), manager.id(),
                                mgrScores, "Assessed based on project outcomes.",
                                false, LocalDate.now().minusMonths(monthsAgo)
                            ));
                        }
                    }
                }

                // Seed Check-Ins to show a history over the same two year period
                if (checkInRepository.findByUserId(engineer.id()).isEmpty()) {
                    for (int i = 0; i < 4; i++) { // Generate 4 checkins
                        int monthsAgo = 3 + (i * 6); // 3, 9, 15, 21 months ago
                        
                        CheckInStatus status = email.equals("charlie@example.com") ? CheckInStatus.READY_FOR_PROMOTION : 
                                             (email.equals("user@example.com") ? CheckInStatus.UNDERPERFORMING : CheckInStatus.ON_TRACK);
                        
                        checkInRepository.save(new CheckIn(null, engineer.id(), manager.id(), 
                            vpExpectations, "Quarterly review summary for " + engineer.fullName(), 
                            status, LocalDate.now().minusMonths(monthsAgo))); // check-in date
                    }

                    // Seed one DRAFT check-in for user@example.com
                    if (email.equals("user@example.com")) {
                        checkInRepository.save(new CheckIn(null, engineer.id(), manager.id(),
                            vpExpectations, "This is a draft check-in notes.",
                            CheckInStatus.DRAFT, LocalDate.now()));
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

                    assessmentRepository.save(new Assessment(
                        null, evidence.id(), manager.id(), // Assessed by self or a peer for example purposes
                        mgrScores, "Excellent strategic impact and leadership.",
                        false, LocalDate.now().minusMonths(monthsAgo)
                    ));
                }
            }
        };
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
}
