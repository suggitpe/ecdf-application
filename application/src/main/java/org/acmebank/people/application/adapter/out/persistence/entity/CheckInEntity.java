package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "check_ins")
@Getter
@Setter
public class CheckInEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private UserEntity manager;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "manager_notes", nullable = false, columnDefinition = "TEXT")
    private String managerNotes;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "check_in_holistic_scores", joinColumns = @JoinColumn(name = "check_in_id"))
    @MapKeyColumn(name = "pillar")
    @Column(name = "score")
    private java.util.Map<String, Integer> holisticScores = new java.util.HashMap<>();
}
