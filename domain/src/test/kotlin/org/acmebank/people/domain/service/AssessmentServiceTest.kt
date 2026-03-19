package org.acmebank.people.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.AssessmentRepository
import org.acmebank.people.domain.port.EvidenceRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class AssessmentServiceTest {

    @Mock
    private lateinit var assessmentRepository: AssessmentRepository

    @Mock
    private lateinit var evidenceRepository: EvidenceRepository

    @InjectMocks
    private lateinit var assessmentService: AssessmentService

    @Test
    fun `should assign third party assessor to submitted evidence`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to Score(3)), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))
        `when`(assessmentRepository.save(any(Assessment::class.java))).thenAnswer { invocation ->
            val assessment = invocation.getArgument<Assessment>(0)
            if (assessment.id() == null) {
                Assessment(UUID.randomUUID(), assessment.evidenceId(), assessment.assessorId(), assessment.assessedScores(), assessment.reviewSummary(), assessment.isThirdParty(), assessment.assessmentDate())
            } else {
                assessment
            }
        }

        // When
        val assessment = assessmentService.assignThirdPartyAssessor(evidenceId, assessorId)

        // Then
        assessment.id shouldNotBe null
        assessment.evidenceId shouldBe evidenceId
        assessment.assessorId shouldBe assessorId
        assessment.isThirdParty shouldBe true
        assessment.assessmentDate shouldBe null
        assessment.assessedScores shouldBe null
        assessment.reviewSummary shouldBe null

        verify(assessmentRepository).save(any(Assessment::class.java))
    }

    @Test
    fun `should fail to assign assessor if evidence is not SUBMITTED`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        val draftEvidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            emptyMap(), emptyList(), emptyList(),
            EvidenceStatus.DRAFT, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(draftEvidence))

        // When & Then
        val exception = shouldThrow<IllegalStateException> {
            assessmentService.assignThirdPartyAssessor(evidenceId, assessorId)
        }
        exception.message shouldBe "Can only assign assessor to SUBMITTED evidence."
    }

    @Test
    fun `should complete a pending third party assessment and update evidence status`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        val pendingAssessmentId = UUID.randomUUID()
        
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to Score(3)), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        val pendingAssessment = Assessment(
            pendingAssessmentId, evidenceId, assessorId, null, null, true, null
        )

        val scores = mapOf(Pillar.THINKS to Score(4))
        val summary = "Great job"

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))
        `when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(Optional.of(pendingAssessment))
        `when`(assessmentRepository.save(any(Assessment::class.java))).thenAnswer { it.getArgument(0) }
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val result = assessmentService.submitAssessment(evidenceId, assessorId, scores, summary)

        // Then
        result.id shouldBe pendingAssessmentId
        result.isThirdParty shouldBe true
        result.assessedScores shouldBe scores
        result.reviewSummary shouldBe summary
        result.assessmentDate shouldNotBe null

        verify(assessmentRepository).save(any(Assessment::class.java))
        // Verify evidence status was also updated to MANAGER_ASSESSED
        val statusCaptor = org.mockito.ArgumentCaptor.forClass(Evidence::class.java)
        verify(evidenceRepository).save(statusCaptor.capture())
        statusCaptor.value.status shouldBe EvidenceStatus.MANAGER_ASSESSED
    }

    @Test
    fun `should create a direct manager assessment if none is pending and update evidence status`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to Score(3)), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        val scores = mapOf(Pillar.THINKS to Score(4))
        val summary = "Manager review looks good"

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))
        `when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(Optional.empty())
        `when`(assessmentRepository.save(any(Assessment::class.java))).thenAnswer { invocation ->
            val assessment = invocation.getArgument<Assessment>(0)
            if (assessment.id() == null) {
                Assessment(UUID.randomUUID(), assessment.evidenceId(), assessment.assessorId(), assessment.assessedScores(), assessment.reviewSummary(), assessment.isThirdParty(), assessment.assessmentDate())
            } else {
                assessment
            }
        }
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val result = assessmentService.submitAssessment(evidenceId, managerId, scores, summary)

        // Then
        result.id shouldNotBe null
        result.evidenceId shouldBe evidenceId
        result.assessorId shouldBe managerId
        result.isThirdParty shouldBe false
        result.assessedScores shouldBe scores
        result.reviewSummary shouldBe summary
        result.assessmentDate shouldNotBe null

        verify(assessmentRepository).save(any(Assessment::class.java))
        val statusCaptor = org.mockito.ArgumentCaptor.forClass(Evidence::class.java)
        verify(evidenceRepository).save(statusCaptor.capture())
        statusCaptor.value.status shouldBe EvidenceStatus.MANAGER_ASSESSED
    }

    @Test
    fun `should fail to submit assessment if scores are empty`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to Score(3)), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))

        // When & Then
        val exception = shouldThrow<IllegalArgumentException> {
            assessmentService.submitAssessment(evidenceId, assessorId, emptyMap(), "Summary")
        }
        exception.message shouldBe "Assessment scores cannot be empty."
    }

    @Test
    fun `should fail to submit assessment if summary is blank`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to Score(3)), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))

        // When & Then
        val exception = shouldThrow<IllegalArgumentException> {
            assessmentService.submitAssessment(evidenceId, assessorId, mapOf(Pillar.THINKS to Score(4)), "   ")
        }
        exception.message shouldBe "Review summary must be provided."
    }

    @Test
    fun `should fail to assess if evidence is already ASSESSED`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        
        val assessedEvidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to Score(3)), emptyList(), emptyList(),
            EvidenceStatus.MANAGER_ASSESSED, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(assessedEvidence))

        // When & Then
        val exception = shouldThrow<IllegalStateException> {
            assessmentService.submitAssessment(evidenceId, assessorId, mapOf(Pillar.THINKS to Score(4)), "Summary")
        }
        exception.message shouldBe "Can only assess evidence that is in SUBMITTED state."
    }

    @Test
    fun `should return pending assessments for ITA`() {
        // Given
        val itaId = UUID.randomUUID()
        val pending1 = Assessment(UUID.randomUUID(), UUID.randomUUID(), itaId, null, null, true, null)
        val pending2 = Assessment(UUID.randomUUID(), UUID.randomUUID(), itaId, null, null, true, null)

        `when`(assessmentRepository.findPendingByAssessorId(itaId)).thenReturn(listOf(pending1, pending2))

        // When
        val result = assessmentService.getPendingAssessmentsForITA(itaId)

        // Then
        result shouldHaveSize 2
        result[0].id shouldBe pending1.id
        result[1].id shouldBe pending2.id
    }
}
