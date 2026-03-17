package org.acmebank.people.application.config;

import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.GradeRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Collections;
import java.util.Random;
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
    public CommandLineRunner seedData(
            UserRepository userRepository, 
            GradeRepository gradeRepository, 
            CheckInRepository checkInRepository,
            EvidenceRepository evidenceRepository,
            AssessmentRepository assessmentRepository) {
        return args -> {
            Random random = new Random();
            String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

            // Ensure Grades exist
            Map<Pillar, Score> expectations = new EnumMap<>(Pillar.class);
            for (Pillar p : Pillar.values()) {
                expectations.put(p, new Score(3));
            }

            Grade managerGrade = gradeRepository.findByNameAndRole("Director", "Management")
                    .orElseGet(() -> gradeRepository.save(new Grade(null, "Director", "Management", expectations)));

            Grade engineerGrade = gradeRepository.findByNameAndRole("Vice President", "Engineering")
                    .orElseGet(() -> gradeRepository.save(new Grade(null, "Vice President", "Engineering", expectations)));

            // Ensure Manager exists (manager@example.com is Mary in Liquibase, we'll keep it as the primary manager)
            User manager = userRepository.findByEmail("manager@example.com")
                    .orElseGet(() -> userRepository.save(new User(null, "manager@example.com", "Manager Mary", managerGrade, null, true)));

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
                    // Update manager association if missing
                    if (engineer.managerId() == null) {
                        engineer = userRepository.save(new User(engineer.id(), engineer.email(), engineer.fullName(), engineer.grade(), manager.id(), engineer.isIta()));
                    }
                }

                // Only seed evidence if they don't have any
                if (evidenceRepository.findByUserId(engineer.id()).isEmpty()) {
                    int count = 4; // Use a fixed count to ensure we cover all pillars
                    for (int i = 0; i < count; i++) {
                        Map<Pillar, Score> selfScores = new EnumMap<>(Pillar.class);
                        Map<Pillar, Score> mgrScores = new EnumMap<>(Pillar.class);
                        
                        // Distribute pillars: each evidence covers a subset of pillars
                        // With 4 evidences and 9 pillars, we can do 2-3 pillars per evidence
                        int pillarsPerEvidence = 3;
                        for (int j = 0; j < pillarsPerEvidence; j++) {
                            int pillarIdx = (i * pillarsPerEvidence + j) % Pillar.values().length;
                            Pillar p = Pillar.values()[pillarIdx];
                            int scoreVal = 2 + random.nextInt(3);
                            selfScores.put(p, new Score(scoreVal));
                            mgrScores.put(p, new Score(scoreVal));
                        }

                        // Make the first evidence SUBMITTED (Draft state for assessment)
                        EvidenceStatus status = (i == 0) ? EvidenceStatus.SUBMITTED : EvidenceStatus.MANAGER_ASSESSED;

                        Evidence evidence = evidenceRepository.save(new Evidence(
                            null, engineer.id(), 
                            "Project " + (char)('A' + i + 1) + " Implementation",
                            loremIpsum, loremIpsum, loremIpsum, loremIpsum,
                            selfScores, Collections.emptyList(), Collections.emptyList(),
                            status,
                            LocalDate.now().minusMonths(i + 1), LocalDate.now().minusMonths(i + 1)
                        ));

                        // Only save assessment if status is MANAGER_ASSESSED
                        if (status == EvidenceStatus.MANAGER_ASSESSED) {
                            assessmentRepository.save(new Assessment(
                                null, evidence.id(), manager.id(),
                                mgrScores, "Assessed based on project outcomes.",
                                false, LocalDate.now().minusMonths(i + 1)
                            ));
                        }
                    }
                }

                // Seed Check-In if missing
                if (checkInRepository.findByUserId(engineer.id()).isEmpty()) {
                    CheckInStatus status = email.equals("charlie@example.com") ? CheckInStatus.READY_FOR_PROMOTION : 
                                         (email.equals("user@example.com") ? CheckInStatus.UNDERPERFORMING : CheckInStatus.ON_TRACK);
                    
                    checkInRepository.save(new CheckIn(null, engineer.id(), manager.id(), 
                        LocalDate.now().minusMonths(3), LocalDate.now(), 
                        expectations, "Quarterly review summary for " + engineer.fullName(), 
                        status, LocalDate.now()));
                }
            }
        };
    }
}
