package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pdp_items")
@Getter
@Setter
public class PdpItemEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_id", nullable = false)
    private CheckInEntity checkIn;

    @Column(name = "targeted_pillar", nullable = false, length = 50)
    private String targetedPillar;

    @Column(name = "gap_description", nullable = false, columnDefinition = "TEXT")
    private String gapDescription;

    @Column(name = "actionable_plan", nullable = false, columnDefinition = "TEXT")
    private String actionablePlan;

    @Column(name = "learning_journey_link", length = 1000)
    private String learningJourneyLink;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDate updatedDate;
}
