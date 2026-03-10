package org.acmebank.people.application.config;

import org.acmebank.people.domain.port.AssessmentRepository;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.EvidenceRepository;
import org.acmebank.people.domain.port.GradeRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.acmebank.people.domain.service.AssessmentService;
import org.acmebank.people.domain.service.CheckInService;
import org.acmebank.people.domain.service.EvidenceService;
import org.acmebank.people.domain.service.GradeService;
import org.acmebank.people.domain.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }

    @Bean
    public GradeService gradeService(GradeRepository gradeRepository) {
        return new GradeService(gradeRepository);
    }

    @Bean
    public EvidenceService evidenceService(EvidenceRepository evidenceRepository) {
        return new EvidenceService(evidenceRepository);
    }

    @Bean
    public AssessmentService assessmentService(AssessmentRepository assessmentRepository, EvidenceRepository evidenceRepository) {
        return new AssessmentService(assessmentRepository, evidenceRepository);
    }

    @Bean
    public CheckInService checkInService(CheckInRepository checkInRepository, EvidenceRepository evidenceRepository, AssessmentRepository assessmentRepository) {
        return new CheckInService(checkInRepository, evidenceRepository, assessmentRepository);
    }
}
