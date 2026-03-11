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

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class DevDataSeeder {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository, GradeRepository gradeRepository) {
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
                userRepository.save(engineer);
            }
        };
    }
}
