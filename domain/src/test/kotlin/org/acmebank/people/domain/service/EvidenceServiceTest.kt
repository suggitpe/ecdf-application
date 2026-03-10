package org.acmebank.people.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.EvidenceRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class EvidenceServiceTest {

    @Mock
    private lateinit var evidenceRepository: EvidenceRepository

    @InjectMocks
    private lateinit var evidenceService: EvidenceService

    @Test
    fun `should create new draft evidence`() {
        // Given
        val userId = UUID.randomUUID()
        val title = "Implemented Caching Layer"
        
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val result = evidenceService.createEvidence(userId, title)

        // Then
        result.userId shouldBe userId
        result.title shouldBe title
        result.status shouldBe EvidenceStatus.DRAFT
        result.selfAssessment shouldBe emptyMap<Pillar, Score>()
        result.id shouldNotBe null
        
        verify(evidenceRepository).save(any(Evidence::class.java))
    }

    @Test
    fun `should fail to submit evidence if self assessment is empty`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val draftEvidence = Evidence(
            evidenceId, 
            UUID.randomUUID(), 
            "Test", 
            "Impact", 
            "Complexity", 
            "Contribution", 
            emptyMap(), 
            emptyList(), 
            emptyList(), 
            EvidenceStatus.DRAFT, 
            LocalDate.now(), 
            LocalDate.now()
        )
        
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(draftEvidence))

        // When & Then
        val exception = shouldThrow<IllegalArgumentException> {
            evidenceService.submitEvidence(evidenceId)
        }
        exception.message shouldBe "Self-assessment cannot be empty before submission."
    }

    @Test
    fun `should successfully submit evidence`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val selfAssessment = mapOf(Pillar.THINKS to Score(3), Pillar.DELIVERS to Score(4))
        val draftEvidence = Evidence(
            evidenceId, 
            UUID.randomUUID(), 
            "Test", 
            "Impact", 
            "Complexity", 
            "Contribution", 
            selfAssessment, 
            emptyList(), 
            emptyList(), 
            EvidenceStatus.DRAFT, 
            LocalDate.now(), 
            LocalDate.now()
        )
        
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(draftEvidence))
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val result = evidenceService.submitEvidence(evidenceId)

        // Then
        result.status shouldBe EvidenceStatus.SUBMITTED
        verify(evidenceRepository).save(any(Evidence::class.java))
    }

    @Test
    fun `should fail to update evidence if already submitted`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val submittedEvidence = Evidence(
            evidenceId, 
            UUID.randomUUID(), 
            "Test", 
            "Impact", 
            "Complexity", 
            "Contribution", 
            mapOf(Pillar.THINKS to Score(3)), 
            emptyList(), 
            emptyList(), 
            EvidenceStatus.SUBMITTED, 
            LocalDate.now(), 
            LocalDate.now()
        )
        
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(submittedEvidence))

        // When & Then
        val exception = shouldThrow<IllegalStateException> {
            evidenceService.updateEvidence(evidenceId, "New Title", "New Impact", "New Complexity", "New Contribution", mapOf(Pillar.THINKS to Score(4)))
        }
        exception.message shouldBe "Cannot modify evidence that is already SUBMITTED or ASSESSED."
    }

    @Test
    fun `should update draft evidence successfully`() {
        // Given
        val evidenceId = UUID.randomUUID()
        val draftEvidence = Evidence(
            evidenceId, 
            UUID.randomUUID(), 
            "Old Title", 
            "Old Impact", 
            "Old Complexity", 
            "Old Contribution", 
            emptyMap(), 
            emptyList(), 
            emptyList(), 
            EvidenceStatus.DRAFT, 
            LocalDate.now(), 
            LocalDate.now()
        )
        
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(draftEvidence))
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val newAssessment = mapOf(Pillar.THINKS to Score(4))
        val result = evidenceService.updateEvidence(evidenceId, "New Title", "New Impact", "New Complexity", "New Contribution", newAssessment)

        // Then
        result.title shouldBe "New Title"
        result.impact shouldBe "New Impact"
        result.selfAssessment shouldBe newAssessment
        verify(evidenceRepository).save(any(Evidence::class.java))
    }
}
