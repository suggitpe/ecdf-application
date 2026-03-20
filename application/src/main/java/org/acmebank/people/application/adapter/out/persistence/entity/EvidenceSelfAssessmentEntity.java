package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "evidence_self_assessment")
@Getter
@Setter
public class EvidenceSelfAssessmentEntity {

    @EmbeddedId
    private EvidenceSelfAssessmentId id;

    @Column(nullable = false)
    private int score;

    @Column(length = 4000)
    private String rationale;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("evidenceId")
    @JoinColumn(name = "evidence_id")
    private EvidenceEntity evidence;
}
