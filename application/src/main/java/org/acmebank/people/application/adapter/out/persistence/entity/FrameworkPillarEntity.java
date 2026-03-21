package org.acmebank.people.application.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "framework_pillars")
@Getter
@Setter
public class FrameworkPillarEntity {

    @Id
    @Column(name = "pillar_name", nullable = false, length = 50)
    private String pillarName;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @OneToMany(mappedBy = "pillar", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER)
    private java.util.List<FrameworkLevelEntity> levels = new java.util.ArrayList<>();
}
