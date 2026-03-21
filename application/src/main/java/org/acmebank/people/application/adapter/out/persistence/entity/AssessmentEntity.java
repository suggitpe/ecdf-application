package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "assessments")
@Getter
@Setter
public class AssessmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private EvidenceEntity evidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessor_id", nullable = false)
    private UserEntity assessor;

    @Column(name = "review_summary", nullable = true, length = 4000)
    private String reviewSummary;

    @Column(name = "is_third_party", nullable = false)
    private boolean isThirdParty;

    @Column(name = "assessment_date", nullable = true)
    private LocalDate assessmentDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "assessment_scores", joinColumns = @JoinColumn(name = "assessment_id"))
    @MapKeyColumn(name = "pillar")
    private java.util.Map<String, AssessedScoreEmbeddable> scores = new java.util.HashMap<>();
}
