package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EvidenceSelfAssessmentId implements Serializable {

    @Column(name = "evidence_id")
    private UUID evidenceId;

    @Column(name = "pillar", length = 50)
    private String pillar;
}
