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
        val assessments = assessmentRepository.findByEvidenceId(savedEvidence.id)

        // Then
        savedAssessment.id shouldNotBe null
        assessments shouldHaveSize 1
        assessments[0].reviewSummary shouldBe "Great work!"
        assessments[0].assessor.email shouldBe "mgr@acmebank.com"
    }

    @Test
    fun `should support multiple assessments for same evidence`() {
        // Given
        val grade = gradeRepository.save(GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        })
        val dev = userRepository.save(UserEntity().apply {
            email = "dev2@acmebank.com"
            fullName = "Jane Dev"
            this.grade = grade
        })
        val manager = userRepository.save(UserEntity().apply {
            email = "mgr2@acmebank.com"
            fullName = "Bob Manager"
            this.grade = grade
        })
        val ita = userRepository.save(UserEntity().apply {
            email = "ita2@acmebank.com"
            fullName = "Ian ITA"
            this.grade = grade
            isIta = true
        })
        val evidence = evidenceRepository.save(EvidenceEntity().apply {
            this.user = dev
            title = "Evidence for multiple assessments"
            description = "Multiple assessment description"
            impact = "High impact"
            complexity = "Complex"
            contribution = "Lead"
            status = "SUBMITTED"
            createdDate = LocalDate.now()
            lastModifiedDate = LocalDate.now()
        })

        // When - Manager assesses
        val managerAssessment = AssessmentEntity().apply {
            this.evidence = evidence
            this.assessor = manager
            reviewSummary = "Manager review"
            isThirdParty = false
            assessmentDate = LocalDate.now()
        }
        assessmentRepository.save(managerAssessment)

        // When - ITA assesses
        val itaAssessment = AssessmentEntity().apply {
            this.evidence = evidence
            this.assessor = ita
            reviewSummary = "ITA review"
            isThirdParty = true
            assessmentDate = LocalDate.now()
        }
        assessmentRepository.save(itaAssessment)

        val assessments = assessmentRepository.findByEvidenceId(evidence.id)

        // Then
        assessments shouldHaveSize 2
        assessments.any { it.reviewSummary == "Manager review" } shouldBe true
        assessments.any { it.reviewSummary == "ITA review" } shouldBe true
    }
}
