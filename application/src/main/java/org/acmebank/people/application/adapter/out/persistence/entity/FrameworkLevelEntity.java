package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "framework_levels")
@Getter
@Setter
public class FrameworkLevelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pillar_name", nullable = false)
    private FrameworkPillarEntity pillar;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "level_description", nullable = false, length = 4000)
    private String levelDescription;

    @Column(name = "evidence_examples", nullable = false, length = 4000)
    private String evidenceExamples;
}
