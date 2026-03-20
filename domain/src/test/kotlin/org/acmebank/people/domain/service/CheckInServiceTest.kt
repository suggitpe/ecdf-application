package org.acmebank.people.domain.service

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.AssessmentRepository
import org.acmebank.people.domain.port.CheckInRepository
import org.acmebank.people.domain.port.EvidenceRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class CheckInServiceTest {

    @Mock
    private lateinit var checkInRepository: CheckInRepository

    @Mock
    private lateinit var evidenceRepository: EvidenceRepository

    @Mock
    private lateinit var assessmentRepository: AssessmentRepository

    @InjectMocks
    private lateinit var checkInService: CheckInService

    @Test
    fun `should evaluate as UNDERPERFORMING if MORE THAN THREE pillars do not meet expectations`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(
            Pillar.THINKS to Score(4),
            Pillar.ENGAGES to Score(4),
            Pillar.INFLUENCES to Score(4),
            Pillar.ACHIEVES to Score(4)
        ))

        // Evidence covers 4 pillars, all below expectation
        val evidenceId = UUID.randomUUID()
        val evidence = createEvidence(userId, evidenceId, LocalDate.now())
        val assessment = createAssessment(evidenceId, mapOf(
            Pillar.THINKS to Score(3),
            Pillar.ENGAGES to Score(3),
            Pillar.INFLUENCES to Score(3),
            Pillar.ACHIEVES to Score(3)
        ), false)

        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED)).thenReturn(listOf(evidence))
        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED)).thenReturn(emptyList())
        lenient().`when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(Optional.of(assessment))
        lenient().`when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "Needs improvement", targetGrade, false)

        // Then
        checkIn.status shouldBe CheckInStatus.UNDERPERFORMING
    }

    @Test
    fun `should evaluate as ON_TRACK if exactly THREE pillars or fewer do not meet expectations`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(
            Pillar.THINKS to Score(4),
            Pillar.ENGAGES to Score(4),
            Pillar.INFLUENCES to Score(4)
        ))

        // 3 pillars below expectations
        val evidenceId = UUID.randomUUID()
        val evidence = createEvidence(userId, evidenceId, LocalDate.now())
        val assessment = createAssessment(evidenceId, mapOf(
            Pillar.THINKS to Score(3),
            Pillar.ENGAGES to Score(3),
            Pillar.INFLUENCES to Score(3)
        ), false)

        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED)).thenReturn(listOf(evidence))
        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED)).thenReturn(emptyList())
        lenient().`when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(Optional.of(assessment))
        lenient().`when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "OK", targetGrade, false)

        // Then
        checkIn.status shouldBe CheckInStatus.ON_TRACK
    }

    @Test
    fun `should evaluate as OVER_PERFORMING if MORE THAN THREE pillars exceed expectations`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(
            Pillar.THINKS to Score(3),
            Pillar.ENGAGES to Score(3),
            Pillar.INFLUENCES to Score(3),
            Pillar.ACHIEVES to Score(3)
        ))

        // 4 pillars above expectations
        val evidenceId = UUID.randomUUID()
        val evidence = createEvidence(userId, evidenceId, LocalDate.now())
        val assessment = createAssessment(evidenceId, mapOf(
            Pillar.THINKS to Score(4),
            Pillar.ENGAGES to Score(4),
            Pillar.INFLUENCES to Score(4),
            Pillar.ACHIEVES to Score(4)
        ), false)

        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED)).thenReturn(listOf(evidence))
        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED)).thenReturn(emptyList())
        lenient().`when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(Optional.of(assessment))
        lenient().`when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "Great job", targetGrade, false)

        // Then
        checkIn.status shouldBe CheckInStatus.OVER_PERFORMING
    }

    @Test
    fun `should evaluate as ON_TRACK if meeting expectations but lacks ITA for promotion`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(
            Pillar.THINKS to Score(3),
            Pillar.DELIVERS to Score(3)
        ))

        // Evidence meets the target grade expectations
        val evidenceId = UUID.randomUUID()
        val evidence = createEvidence(userId, evidenceId, LocalDate.now())
        // Assessment is not third party
        val assessment = createAssessment(evidenceId, mapOf(Pillar.THINKS to Score(4), Pillar.DELIVERS to Score(4)), false)

        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED)).thenReturn(listOf(evidence))
        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED)).thenReturn(emptyList())
        lenient().`when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(Optional.of(assessment))
        lenient().`when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "Doing well", targetGrade, false)

        // Then
        checkIn.status shouldBe CheckInStatus.ON_TRACK
        verify(checkInRepository).save(any(CheckIn::class.java))
    }

    @Test
    fun `should evaluate as READY_FOR_PROMOTION if expectations are met AND has ITA assessment`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(
            Pillar.THINKS to Score(3)
        ))

        val evidenceId = UUID.randomUUID()
        val evidence = createEvidence(userId, evidenceId, LocalDate.now())
        // Assessment IS third party and meets scores
        val assessment = createAssessment(evidenceId, mapOf(Pillar.THINKS to Score(4)), true)

        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED)).thenReturn(listOf(evidence))
        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED)).thenReturn(emptyList())
        lenient().`when`(assessmentRepository.findByEvidenceId(evidenceId)).thenReturn(Optional.of(assessment))
        lenient().`when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "Ready to go up", targetGrade, false)

        // Then
        checkIn.status shouldBe CheckInStatus.READY_FOR_PROMOTION
        verify(checkInRepository).save(any(CheckIn::class.java))
    }

    @Test
    fun `should exclude evidence assessments older than 3 years`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(
            Pillar.THINKS to Score(3)
        ))

        val oldEvidenceId = UUID.randomUUID()
        // Evidence is 4 years old
        val oldEvidence = createEvidence(userId, oldEvidenceId, LocalDate.now().minusYears(4))
        // Assessment scores indicate ready, but it shouldn't be counted
        val oldAssessment = createAssessment(oldEvidenceId, mapOf(Pillar.THINKS to Score(5)), true)

        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED)).thenReturn(listOf(oldEvidence))
        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED)).thenReturn(emptyList())
        lenient().`when`(assessmentRepository.findByEvidenceId(oldEvidenceId)).thenReturn(Optional.of(oldAssessment))
        lenient().`when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "Old evidence", targetGrade, false)

        // Then
        // Without old evidence, they have 0 expectations met for THINKS, which is 1 missing pillar.
        // But 1 missing pillar is <= 3, so they are ON_TRACK (or not underperforming).
        checkIn.status shouldBe CheckInStatus.ON_TRACK
        checkIn.holisticScores.isEmpty() shouldBe true
    }

    @Test
    fun `should aggregate highest most recent assessments for each pillar`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Mid", "Developer", mapOf(
            Pillar.THINKS to Score(2)
        ))

        val evidence1Id = UUID.randomUUID()
        val evidence1 = createEvidence(userId, evidence1Id, LocalDate.now().minusDays(10))
        val assessment1 = createAssessment(evidence1Id, mapOf(Pillar.THINKS to Score(3)), false)

        val evidence2Id = UUID.randomUUID()
        val evidence2 = createEvidence(userId, evidence2Id, LocalDate.now().minusDays(5))
        // This assessment has a lower score but is more recent? We should aggregate the HIGHLIGHT of the best valid evidence.
        // Actually, typical ECDF aggregates the best valid score they've achieved within the window.
        val assessment2 = createAssessment(evidence2Id, mapOf(Pillar.THINKS to Score(4)), false)

        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED)).thenReturn(listOf(evidence1, evidence2))
        lenient().`when`(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED)).thenReturn(emptyList())
        lenient().`when`(assessmentRepository.findByEvidenceId(evidence1Id)).thenReturn(Optional.of(assessment1))
        lenient().`when`(assessmentRepository.findByEvidenceId(evidence2Id)).thenReturn(Optional.of(assessment2))
        lenient().`when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "Notes", targetGrade, false)

        // Then
        checkIn.holisticScores[Pillar.THINKS] shouldBe Score(4) // It should take the max valid score
    }

    @Test
    fun `should create check-in with DRAFT status when isDraft is true`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", emptyMap())

        `when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val checkIn = checkInService.createCheckIn(userId, managerId, "Draft notes", targetGrade, true)

        // Then
        checkIn.status shouldBe CheckInStatus.DRAFT
        checkIn.managerNotes shouldBe "Draft notes"
    }

    @Test
    fun `should finalize a draf check-in and evaluate status`() {
        // Given
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val checkInId = UUID.randomUUID()
        val targetGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(Pillar.THINKS to Score(3)))
        
        val draft = CheckIn(checkInId, userId, managerId, LocalDate.now(), LocalDate.now(), 
            mapOf(Pillar.THINKS to Score(4)), "Initial notes", CheckInStatus.DRAFT, LocalDate.now())

        `when`(checkInRepository.findById(checkInId)).thenReturn(Optional.of(draft))
        `when`(checkInRepository.save(any(CheckIn::class.java))).thenAnswer { it.getArgument(0) }
        `when`(evidenceRepository.findByUserIdAndStatus(any(), any())).thenReturn(emptyList()) // Simple case for status eval

        // When
        val finalized = checkInService.updateCheckIn(checkInId, "Final notes", targetGrade, true)

        // Then
        finalized.status shouldBe CheckInStatus.ON_TRACK // Meets 1/1 pillars, not ITA, so ON_TRACK
        finalized.managerNotes shouldBe "Final notes"
    }

    private fun createEvidence(userId: UUID, evidenceId: UUID, createdDate: LocalDate): Evidence {
        return Evidence(
            evidenceId, userId, "Title", "Description", "Impact", "Complexity", "Contribution",
            emptyMap(), emptyList(), emptyList(), EvidenceStatus.MANAGER_ASSESSED, createdDate, createdDate
        )
    }

    private fun createAssessment(evidenceId: UUID, scores: Map<Pillar, Score>, isThirdParty: Boolean): Assessment {
        return Assessment(
            UUID.randomUUID(), evidenceId, UUID.randomUUID(), scores, "Summary", isThirdParty, LocalDate.now()
        )
    }
}
