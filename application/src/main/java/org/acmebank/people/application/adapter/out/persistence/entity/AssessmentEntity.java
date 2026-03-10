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
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false, unique = true)
    private EvidenceEntity evidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessor_id", nullable = false)
    private UserEntity assessor;

    @Column(name = "review_summary", nullable = false, columnDefinition = "TEXT")
    private String reviewSummary;

    @Column(name = "is_third_party", nullable = false)
    private boolean isThirdParty;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "assessment_scores", joinColumns = @JoinColumn(name = "assessment_id"))
    @MapKeyColumn(name = "pillar")
    @Column(name = "score")
    private java.util.Map<String, Integer> scores = new java.util.HashMap<>();
}
