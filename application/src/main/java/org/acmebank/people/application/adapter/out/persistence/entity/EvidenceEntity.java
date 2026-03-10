package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "evidence")
@Getter
@Setter
public class EvidenceEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String impact;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String complexity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contribution;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private LocalDate lastModifiedDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "evidence_links", joinColumns = @JoinColumn(name = "evidence_id"))
    @Column(name = "url")
    private java.util.List<String> links = new java.util.ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "evidence_attachments", joinColumns = @JoinColumn(name = "evidence_id"))
    @Column(name = "file_path")
    private java.util.List<String> attachments = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "evidence", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<EvidenceSelfAssessmentEntity> selfAssessments = new java.util.ArrayList<>();
}
