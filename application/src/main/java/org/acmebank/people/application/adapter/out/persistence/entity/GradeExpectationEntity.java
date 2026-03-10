package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "grade_expectations")
@Getter
@Setter
public class GradeExpectationEntity {

    @EmbeddedId
    private GradeExpectationId id;

    @Column(name = "expected_score", nullable = false)
    private int expectedScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gradeId")
    @JoinColumn(name = "grade_id")
    private GradeEntity grade;
}
