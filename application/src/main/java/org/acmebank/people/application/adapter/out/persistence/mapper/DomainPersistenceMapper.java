package org.acmebank.people.application.adapter.out.persistence.mapper;

import org.acmebank.people.application.adapter.out.persistence.entity.*;
import org.acmebank.people.domain.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DomainPersistenceMapper {

    public static Grade toDomainGrade(GradeEntity entity) {
        if (entity == null)
            return null;
        Map<Pillar, Score> expectations = entity.getExpectations().stream()
                .collect(Collectors.toMap(
                        exp -> Pillar.valueOf(exp.getId().getPillar()),
                        exp -> new Score(exp.getExpectedScore())));
        return new Grade(entity.getId(), entity.getName(), entity.getRole(), expectations);
    }

    public static GradeEntity toGradeEntity(Grade domain) {
        if (domain == null)
            return null;
        GradeEntity entity = new GradeEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setRole(domain.role());

        domain.expectations().forEach((pillar, score) -> {
            GradeExpectationEntity exp = new GradeExpectationEntity();
            exp.setId(new GradeExpectationId(domain.id(), pillar.name()));
            exp.setExpectedScore(score.value());
            exp.setGrade(entity);
            entity.getExpectations().add(exp);
        });
        return entity;
    }

    public static User toDomainUser(UserEntity entity) {
        if (entity == null)
            return null;
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getFullName(),
                toDomainGrade(entity.getGrade()),
                entity.getManager() != null ? entity.getManager().getId() : null,
                entity.isIta());
    }

    public static UserEntity toUserEntity(User domain, GradeEntity gradeEntity, UserEntity managerEntity) {
        if (domain == null)
            return null;
        UserEntity entity = new UserEntity();
        entity.setId(domain.id());
        entity.setEmail(domain.email());
        entity.setFullName(domain.fullName());
        entity.setIta(domain.isIta());
        entity.setGrade(gradeEntity);
        entity.setManager(managerEntity);
        return entity;
    }

    public static Evidence toDomainEvidence(EvidenceEntity entity) {
        if (entity == null)
            return null;
        Map<Pillar, Score> selfAssessments = entity.getSelfAssessments().stream()
                .collect(Collectors.toMap(
                        sa -> Pillar.valueOf(sa.getId().getPillar()),
                        sa -> new Score(sa.getScore())));
        return new Evidence(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTitle(),
                entity.getImpact(),
                entity.getComplexity(),
                entity.getContribution(),
                selfAssessments,
                entity.getLinks() != null ? new java.util.ArrayList<>(entity.getLinks()) : new java.util.ArrayList<>(),
                entity.getAttachments() != null ? new java.util.ArrayList<>(entity.getAttachments())
                        : new java.util.ArrayList<>(),
                EvidenceStatus.valueOf(entity.getStatus()),
                entity.getCreatedDate(),
                entity.getLastModifiedDate());
    }

    public static EvidenceEntity toEvidenceEntity(Evidence domain, UserEntity userEntity) {
        if (domain == null)
            return null;
        EvidenceEntity entity = new EvidenceEntity();
        entity.setId(domain.id());
        entity.setUser(userEntity);
        entity.setTitle(domain.title());
        entity.setImpact(domain.impact());
        entity.setComplexity(domain.complexity());
        entity.setContribution(domain.contribution());
        entity.setStatus(domain.status().name());
        entity.setCreatedDate(domain.createdDate());
        entity.setLastModifiedDate(domain.lastModifiedDate());
        entity.setLinks(
                domain.links() != null ? new java.util.ArrayList<>(domain.links()) : new java.util.ArrayList<>());
        entity.setAttachments(domain.attachmentPaths() != null ? new java.util.ArrayList<>(domain.attachmentPaths())
                : new java.util.ArrayList<>());

        if (domain.selfAssessment() != null) {
            domain.selfAssessment().forEach((pillar, score) -> {
                EvidenceSelfAssessmentEntity sa = new EvidenceSelfAssessmentEntity();
                sa.setId(new EvidenceSelfAssessmentId(domain.id(), pillar.name()));
                sa.setScore(score.value());
                sa.setEvidence(entity);
                entity.getSelfAssessments().add(sa);
            });
        }
        return entity;
    }

    public static Assessment toDomainAssessment(AssessmentEntity entity) {
        if (entity == null)
            return null;
        Map<Pillar, Score> scores = entity.getScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Pillar.valueOf(e.getKey()),
                        e -> new Score(e.getValue())));
        return new Assessment(
                entity.getId(),
                entity.getEvidence().getId(),
                entity.getAssessor().getId(),
                scores,
                entity.getReviewSummary(),
                entity.isThirdParty(),
                entity.getAssessmentDate());
    }

    public static AssessmentEntity toAssessmentEntity(Assessment domain, EvidenceEntity evidenceEntity,
            UserEntity assessorEntity) {
        if (domain == null)
            return null;
        AssessmentEntity entity = new AssessmentEntity();
        entity.setId(domain.id());
        entity.setEvidence(evidenceEntity);
        entity.setAssessor(assessorEntity);
        entity.setReviewSummary(domain.reviewSummary());
        entity.setThirdParty(domain.isThirdParty());
        entity.setAssessmentDate(domain.assessmentDate());

        if (domain.assessedScores() != null) {
            domain.assessedScores().forEach((pillar, score) -> {
                entity.getScores().put(pillar.name(), score.value());
            });
        }
        return entity;
    }

    public static CheckIn toDomainCheckIn(CheckInEntity entity) {
        if (entity == null)
            return null;
        Map<Pillar, Score> scores = entity.getHolisticScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Pillar.valueOf(e.getKey()),
                        e -> new Score(e.getValue())));
        return new CheckIn(
                entity.getId(),
                entity.getUser().getId(),
                entity.getManager().getId(),
                entity.getPeriodStartDate(),
                entity.getPeriodEndDate(),
                scores,
                entity.getManagerNotes(),
                CheckInStatus.valueOf(entity.getStatus()),
                entity.getCheckInDate());
    }

    public static CheckInEntity toCheckInEntity(CheckIn domain, UserEntity userEntity, UserEntity managerEntity) {
        if (domain == null)
            return null;
        CheckInEntity entity = new CheckInEntity();
        entity.setId(domain.id());
        entity.setUser(userEntity);
        entity.setManager(managerEntity);
        entity.setPeriodStartDate(domain.periodStartDate());
        entity.setPeriodEndDate(domain.periodEndDate());
        entity.setManagerNotes(domain.managerNotes());
        entity.setStatus(domain.status().name());
        entity.setCheckInDate(domain.checkInDate());

        if (domain.holisticScores() != null) {
            domain.holisticScores().forEach((pillar, score) -> {
                entity.getHolisticScores().put(pillar.name(), score.value());
            });
        }
        return entity;
    }

    public static PdpItem toDomainPdpItem(PdpItemEntity entity) {
        if (entity == null)
            return null;
        return new PdpItem(
                entity.getId(),
                entity.getUser().getId(),
                entity.getCheckIn().getId(),
                Pillar.valueOf(entity.getTargetedPillar()),
                entity.getGapDescription(),
                entity.getActionablePlan(),
                entity.getLearningJourneyLink(),
                entity.isCompleted(),
                entity.getCreatedDate(),
                entity.getUpdatedDate());
    }

    public static PdpItemEntity toPdpItemEntity(PdpItem domain, UserEntity userEntity, CheckInEntity checkInEntity) {
        if (domain == null)
            return null;
        PdpItemEntity entity = new PdpItemEntity();
        entity.setId(domain.id());
        entity.setUser(userEntity);
        entity.setCheckIn(checkInEntity);
        entity.setTargetedPillar(domain.targetedPillar().name());
        entity.setGapDescription(domain.gapDescription());
        entity.setActionablePlan(domain.actionablePlan());
        entity.setLearningJourneyLink(domain.learningJourneyLink());
        entity.setCompleted(domain.isCompleted());
        entity.setCreatedDate(domain.createdDate());
        entity.setUpdatedDate(domain.updatedDate());
        return entity;
    }
}
