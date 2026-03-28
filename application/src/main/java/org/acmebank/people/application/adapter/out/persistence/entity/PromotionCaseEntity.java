package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.acmebank.people.domain.PromotionStatus;

import java.util.UUID;

@Entity
@Table(name = "promotion_cases")
@Getter
@Setter
public class PromotionCaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private UserEntity candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private UserEntity manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_grade_id", nullable = false)
    private GradeEntity targetGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_period_id", nullable = false)
    private PromotionPeriodEntity promotionPeriod;

    @Column(name = "rationale", nullable = false, length = 4000)
    private String rationale;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PromotionStatus status;
}
