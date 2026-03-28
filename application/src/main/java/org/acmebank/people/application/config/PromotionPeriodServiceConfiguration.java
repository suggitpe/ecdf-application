package org.acmebank.people.application.config;

import org.acmebank.people.domain.port.PromotionPeriodRepository;
import org.acmebank.people.domain.service.PromotionPeriodService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromotionPeriodServiceConfiguration {

    @Bean
    public PromotionPeriodService promotionPeriodService(PromotionPeriodRepository repository) {
        return new PromotionPeriodService(repository);
    }
}
