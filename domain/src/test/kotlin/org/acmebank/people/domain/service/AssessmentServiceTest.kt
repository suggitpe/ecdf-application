package org.acmebank.people.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
    fun `should assign third party assessor to manager assessed evidence`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to EvidenceRating(Score(3), "")), emptyList(), emptyList(),
            EvidenceStatus.MANAGER_ASSESSED, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))
        `when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(emptyList())
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
    fun `should fail to assign assessor if evidence is not MANAGER_ASSESSED`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        val submittedEvidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            emptyMap(), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(submittedEvidence))

        // When & Then
        val exception = shouldThrow<IllegalStateException> {
            assessmentService.assignThirdPartyAssessor(evidenceId, assessorId)
        }
        exception.message shouldBe "Can only assign ITA to evidence that has been MANAGER_ASSESSED."
    }

    @Test
    fun `should complete a pending third party assessment and update evidence status to INDEPENDENTLY_ASSESSED`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        val pendingAssessmentId = UUID.randomUUID()
        
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to EvidenceRating(Score(3), "")), emptyList(), emptyList(),
            EvidenceStatus.MANAGER_ASSESSED, LocalDate.now(), LocalDate.now()
        )

        val pendingAssessment = Assessment(
            pendingAssessmentId, evidenceId, assessorId, null, null, true, null
        )

        val scores = mapOf(Pillar.THINKS to Score(4))
        val summary = "Great job"

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))
        `when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(listOf(pendingAssessment))
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
        // Verify evidence status was updated to INDEPENDENTLY_ASSESSED
        val statusCaptor = org.mockito.ArgumentCaptor.forClass(Evidence::class.java)
        verify(evidenceRepository).save(statusCaptor.capture())
        statusCaptor.value.status shouldBe EvidenceStatus.INDEPENDENTLY_ASSESSED
    }

    @Test
    fun `should create a direct manager assessment if none is pending and update evidence status to MANAGER_ASSESSED`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        
        val evidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to EvidenceRating(Score(3), "")), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        val scores = mapOf(Pillar.THINKS to Score(4))
        val summary = "Manager review looks good"

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidence))
        `when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(emptyList())
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
            mapOf(Pillar.THINKS to EvidenceRating(Score(3), "")), emptyList(), emptyList(),
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
            mapOf(Pillar.THINKS to EvidenceRating(Score(3), "")), emptyList(), emptyList(),
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
    fun `should fail to assess if evidence is already INDEPENDENTLY_ASSESSED (Manager attempt)`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val assessorId = UUID.randomUUID()
        
        val assessedEvidence = Evidence(
            evidenceId, UUID.randomUUID(), "Test", "Description", "Impact", "Complexity", "Contribution",
            mapOf(Pillar.THINKS to EvidenceRating(Score(3), "")), emptyList(), emptyList(),
            EvidenceStatus.MANAGER_ASSESSED, LocalDate.now(), LocalDate.now()
        )

        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(assessedEvidence))
        `when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(emptyList())

        // When & Then
        val exception = shouldThrow<IllegalStateException> {
            assessmentService.submitAssessment(evidenceId, assessorId, mapOf(Pillar.THINKS to Score(4)), "Summary")
        }
        exception.message shouldBe "Manager can only assess evidence in SUBMITTED state."
    }
}
