package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.AssessmentEntity
import org.acmebank.people.application.adapter.out.persistence.entity.EvidenceEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity
import org.acmebank.people.application.adapter.out.persistence.repository.AssessmentJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.EvidenceJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.optional.shouldBePresent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class AssessmentRepositoryIntegrationTest {

    @Autowired
    private lateinit var assessmentRepository: AssessmentJpaRepository

    @Autowired
    private lateinit var evidenceRepository: EvidenceJpaRepository

    @Autowired
    private lateinit var userRepository: UserJpaRepository

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save and find assessment by evidence id`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        }
        val savedGrade = gradeRepository.save(grade)

        val dev = UserEntity().apply {
            email = "dev@acmebank.com"
            fullName = "Jane Dev"
            isIta = false
            this.grade = savedGrade
        }
        val savedDev = userRepository.save(dev)

        val manager = UserEntity().apply {
            email = "mgr@acmebank.com"
            fullName = "Bob Manager"
            isIta = true
            this.grade = savedGrade
        }
        val savedManager = userRepository.save(manager)

        val evidence = EvidenceEntity().apply {
            this.user = savedDev
            title = "Project Y Refactoring"
            description = "Detailed description of project Y"
            impact = "Reduced tech debt"
            complexity = "Medium"
            contribution = "Sole contributor"
            status = "SUBMITTED"
            createdDate = LocalDate.now()
            lastModifiedDate = LocalDate.now()
        }
        val savedEvidence = evidenceRepository.save(evidence)

        val assessment = AssessmentEntity().apply {
            this.evidence = savedEvidence
            this.assessor = savedManager
            reviewSummary = "Great work!"
            isThirdParty = false
            assessmentDate = LocalDate.now()
        }

        // When
        val savedAssessment = assessmentRepository.save(assessment)
        val foundAssessment = assessmentRepository.findByEvidenceId(savedEvidence.id)

        // Then
        savedAssessment.id shouldNotBe null
        foundAssessment.shouldBePresent()
        foundAssessment.get().reviewSummary shouldBe "Great work!"
        foundAssessment.get().assessor.email shouldBe "mgr@acmebank.com"
    }

    @Test
    fun `should find pending assessments by assessor id`() {
        // Given
        val grade = GradeEntity().apply {
            name = "VP"
            role = "Manager"
        }
        val savedGrade = gradeRepository.save(grade)

        val dev = UserEntity().apply {
            email = "dev2@acmebank.com"
            fullName = "John Dev"
            isIta = false
            this.grade = savedGrade
        }
        val savedDev = userRepository.save(dev)

        val ita = UserEntity().apply {
            email = "ita@acmebank.com"
            fullName = "Alice Assessor"
            isIta = true
            this.grade = savedGrade
        }
        val savedIta = userRepository.save(ita)

        val evidence = EvidenceEntity().apply {
            this.user = savedDev
            title = "Project Z"
            description = "Some description"
            impact = "High"
            complexity = "High"
            contribution = "Lead"
            status = "SUBMITTED"
            createdDate = LocalDate.now()
            lastModifiedDate = LocalDate.now()
        }
        val savedEvidence = evidenceRepository.save(evidence)

        val pendingAssessment = AssessmentEntity().apply {
            this.evidence = savedEvidence
            this.assessor = savedIta
            isThirdParty = true
            // assessmentDate and reviewSummary left null
        }
        assessmentRepository.save(pendingAssessment)

        // When
        val pendingAssessments = assessmentRepository.findByAssessorIdAndAssessmentDateIsNull(savedIta.id)

        // Then
        pendingAssessments shouldHaveSize 1
        pendingAssessments[0].assessor.id shouldBe savedIta.id
        pendingAssessments[0].assessmentDate shouldBe null
    }
}
