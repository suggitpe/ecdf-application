package org.acmebank.people.application.config;

import org.acmebank.people.domain.port.*;
import org.acmebank.people.domain.service.PromotionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromotionServiceConfiguration {

    @Bean
    public PromotionService promotionService(PromotionCaseRepository promotionCaseRepository,
                                             PromotionPeriodRepository promotionPeriodRepository,
                                             UserRepository userRepository,
                                             GradeRepository gradeRepository) {
        return new PromotionService(promotionCaseRepository, promotionPeriodRepository, userRepository, gradeRepository);
    }
}
