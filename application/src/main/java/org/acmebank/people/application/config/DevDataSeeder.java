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
import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.CheckInStatus;
import org.acmebank.people.domain.port.CheckInRepository;

@Configuration
public class DevDataSeeder {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository, GradeRepository gradeRepository, CheckInRepository checkInRepository) {
        return args -> {
            if (userRepository.findAll().isEmpty()) {
                Map<Pillar, Score> expectations = new EnumMap<>(Pillar.class);
                for (Pillar p : Pillar.values()) {
                    expectations.put(p, new Score(3));
                }

                Grade managerGrade = new Grade(null, "Engineering Manager", "Management", expectations);
                managerGrade = gradeRepository.save(managerGrade);

                Grade engineerGrade = new Grade(null, "Software Engineer", "Engineering", expectations);
                engineerGrade = gradeRepository.save(engineerGrade);

                User manager = new User(null, "manager@example.com", "Manager Alice", managerGrade, null, true);
                manager = userRepository.save(manager);

                User engineer = new User(null, "user@example.com", "Engineer Bob", engineerGrade, manager.id(), false);
                engineer = userRepository.save(engineer);
                
                User charlie = new User(null, "charlie@example.com", "Engineer Charlie", engineerGrade, manager.id(), false);
                charlie = userRepository.save(charlie);
                
                User dave = new User(null, "dave@example.com", "Engineer Dave", engineerGrade, manager.id(), false);
                dave = userRepository.save(dave);

                // Add Check-Ins
                LocalDate now = LocalDate.now();
                
                // Bob: On Track
                CheckIn bobCheckIn = new CheckIn(null, engineer.id(), manager.id(), now.minusMonths(3), now, expectations, "Great work", CheckInStatus.ON_TRACK, now);
                checkInRepository.save(bobCheckIn);
                
                // Charlie: Ready for Promo
                CheckIn charlieCheckIn = new CheckIn(null, charlie.id(), manager.id(), now.minusMonths(3), now, expectations, "Ready for the next step", CheckInStatus.READY_FOR_PROMOTION, now);
                checkInRepository.save(charlieCheckIn);
                
                // Dave: Skill Gap
                CheckIn daveCheckIn = new CheckIn(null, dave.id(), manager.id(), now.minusMonths(3), now, expectations, "Needs improvement on a few pillars", CheckInStatus.UNDERPERFORMING, now);
                checkInRepository.save(daveCheckIn);
            }
        };
    }
}
