package org.acmebank.people.application.adapter.out.persistence.mapper;

import org.acmebank.people.application.adapter.out.persistence.entity.*;
import org.acmebank.people.domain.*;

import java.util.Map;
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
                entity.isIta(),
                entity.isPromotionCoordinator());
    }

    public static UserEntity toUserEntity(User domain, GradeEntity gradeEntity, UserEntity managerEntity) {
        if (domain == null)
            return null;
        UserEntity entity = new UserEntity();
        entity.setId(domain.id());
        entity.setEmail(domain.email());
        entity.setFullName(domain.fullName());
        entity.setIta(domain.isIta());
        entity.setPromotionCoordinator(domain.isPromotionCoordinator());
        entity.setGrade(gradeEntity);
        entity.setManager(managerEntity);
        return entity;
    }

    public static Evidence toDomainEvidence(EvidenceEntity entity) {
        if (entity == null)
            return null;
        Map<Pillar, EvidenceRating> selfAssessments = entity.getSelfAssessments().stream()
                .collect(Collectors.toMap(
                        sa -> Pillar.valueOf(sa.getId().getPillar()),
                        sa -> new EvidenceRating(new Score(sa.getScore()), sa.getRationale() == null ? "" : sa.getRationale())));
        return new Evidence(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTitle(),
                entity.getDescription(),
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
        updateEvidenceEntity(entity, domain, userEntity);
        return entity;
    }

    public static void updateEvidenceEntity(EvidenceEntity entity, Evidence domain, UserEntity userEntity) {
        if (domain.id() != null) {
            entity.setId(domain.id());
        }
        entity.setUser(userEntity);
        entity.setTitle(domain.title());
        entity.setDescription(domain.description());
        entity.setImpact(domain.impact());
        entity.setComplexity(domain.complexity());
        entity.setContribution(domain.contribution());
        entity.setStatus(domain.status().name());
        entity.setCreatedDate(domain.createdDate());
        entity.setLastModifiedDate(domain.lastModifiedDate());
        
        // Update collections
        entity.getLinks().clear();
        if (domain.links() != null) {
            entity.getLinks().addAll(domain.links());
        }
        
        entity.getAttachments().clear();
        if (domain.attachmentPaths() != null) {
            entity.getAttachments().addAll(domain.attachmentPaths());
        }

        // Update self-assessments - we clear and rebuild to maintain consistency with the map
        entity.getSelfAssessments().clear();
        if (domain.selfAssessment() != null) {
            domain.selfAssessment().forEach((pillar, rating) -> {
                EvidenceSelfAssessmentEntity sa = new EvidenceSelfAssessmentEntity();
                sa.setId(new EvidenceSelfAssessmentId(domain.id(), pillar.name()));
                sa.setScore(rating.score().value());
                sa.setRationale(rating.rationale());
                sa.setEvidence(entity);
                entity.getSelfAssessments().add(sa);
            });
        }
    }

    public static Assessment toDomainAssessment(AssessmentEntity entity) {
        if (entity == null)
            return null;
        Map<Pillar, EvidenceRating> scores = entity.getScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Pillar.valueOf(e.getKey()),
                        e -> new EvidenceRating(new Score(e.getValue().getScore()), e.getValue().getRationale() == null ? "" : e.getValue().getRationale())));
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
        updateAssessmentEntity(entity, domain, evidenceEntity, assessorEntity);
        return entity;
    }

    public static void updateAssessmentEntity(AssessmentEntity entity, Assessment domain, EvidenceEntity evidenceEntity,
            UserEntity assessorEntity) {
        if (domain.id() != null) {
            entity.setId(domain.id());
        }
        entity.setEvidence(evidenceEntity);
        entity.setAssessor(assessorEntity);
        entity.setReviewSummary(domain.reviewSummary());
        entity.setThirdParty(domain.isThirdParty());
        entity.setAssessmentDate(domain.assessmentDate());

        // Update scores map
        entity.getScores().clear();
        if (domain.assessedScores() != null) {
            domain.assessedScores().forEach((pillar, rating) -> {
                entity.getScores().put(pillar.name(), new AssessedScoreEmbeddable(rating.score().value(), rating.rationale()));
            });
        }
    }

    public static CheckIn toDomainCheckIn(CheckInEntity entity) {
        if (entity == null)
            return null;
        Map<Pillar, PillarScoreInfo> scores = entity.getHolisticScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Pillar.valueOf(e.getKey()),
                        e -> new PillarScoreInfo(new Score(e.getValue().getScore()), e.getValue().getEvidenceId())));
        return new CheckIn(
                entity.getId(),
                entity.getUser().getId(),
                entity.getManager().getId(),
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
        entity.setManagerNotes(domain.managerNotes());
        entity.setStatus(domain.status().name());
        entity.setCheckInDate(domain.checkInDate());

        if (domain.holisticScores() != null) {
            domain.holisticScores().forEach((pillar, info) -> {
                entity.getHolisticScores().put(pillar.name(), new CheckInEntity.PillarScoreValue(info.score().value(), info.evidenceId()));
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

    public static PromotionPeriod toDomainPromotionPeriod(PromotionPeriodEntity entity) {
        if (entity == null)
            return null;
        return new PromotionPeriod(
                entity.getId(),
                entity.getTitle(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus());
    }

    public static void updatePromotionPeriodEntity(PromotionPeriodEntity entity, PromotionPeriod domain) {
        if (domain.id() != null) {
            entity.setId(domain.id());
        }
        entity.setTitle(domain.title());
        entity.setStartDate(domain.startDate());
        entity.setEndDate(domain.endDate());
        entity.setStatus(domain.status());
    }
}
