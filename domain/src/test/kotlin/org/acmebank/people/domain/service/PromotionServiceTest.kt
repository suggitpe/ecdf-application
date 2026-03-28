package org.acmebank.people.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.*
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
class PromotionServiceTest {

    @Mock
    private lateinit var promotionCaseRepository: PromotionCaseRepository

    @Mock
    private lateinit var promotionPeriodRepository: PromotionPeriodRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var gradeRepository: GradeRepository

    @Mock
    private lateinit var checkInRepository: CheckInRepository

    @InjectMocks
    private lateinit var promotionService: PromotionService

    @Test
    fun `should successfully propose a candidate and update check-in status`() {
        // Given
        val candidateId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val targetGradeId = UUID.randomUUID()
        val periodId = UUID.randomUUID()
        val rationale = "Exceptional performance"

        val activePeriod = PromotionPeriod(periodId, "Q1 2026", LocalDate.now(), LocalDate.now().plusMonths(1), PromotionPeriodStatus.OPEN)
        val candidate = User(candidateId, "dev@test.com", "Dev", null, managerId, false, false)
        val targetGrade = Grade(targetGradeId, "Lead", "Engineer", emptyMap())
        val latestCheckIn = CheckIn(UUID.randomUUID(), candidateId, managerId, emptyMap(), "Notes", CheckInStatus.READY_FOR_PROMOTION, LocalDate.now())

        `when`(promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)).thenReturn(Optional.of(activePeriod))
        `when`(userRepository.findById(candidateId)).thenReturn(Optional.of(candidate))
        `when`(gradeRepository.findById(targetGradeId)).thenReturn(Optional.of(targetGrade))
        `when`(promotionCaseRepository.save(any(PromotionCase::class.java))).thenAnswer { it.getArgument(0) }
        `when`(checkInRepository.findByUserId(candidateId)).thenReturn(listOf(latestCheckIn))

        // When
        val result = promotionService.proposeCandidate(candidateId, managerId, targetGradeId, rationale)

        // Then
        result.status shouldBe PromotionStatus.PROPOSED
        verify(checkInRepository).save(any(CheckIn::class.java))
    }


    @Test
    fun `should fail if no open promotion period exists`() {
        `when`(promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)).thenReturn(Optional.empty())

        val exception = shouldThrow<IllegalStateException> {
            promotionService.proposeCandidate(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Rationale")
        }
        exception.message shouldBe "Promotion proposals can only be submitted during an OPEN promotion period."
    }

    @Test
    fun `should fail if candidate is not a direct report`() {
        val candidateId = UUID.randomUUID()
        val otherManagerId = UUID.randomUUID()
        val actualManagerId = UUID.randomUUID()
        val activePeriod = PromotionPeriod(UUID.randomUUID(), "Q1", LocalDate.now(), LocalDate.now(), PromotionPeriodStatus.OPEN)
        val candidate = User(candidateId, "dev@test.com", "Dev", null, actualManagerId, false, false)

        `when`(promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)).thenReturn(Optional.of(activePeriod))
        `when`(userRepository.findById(candidateId)).thenReturn(Optional.of(candidate))

        val exception = shouldThrow<IllegalArgumentException> {
            promotionService.proposeCandidate(candidateId, otherManagerId, UUID.randomUUID(), "Rationale")
        }
        exception.message shouldBe "Manager is not authorized to propose this candidate (not a direct report)."
    }

    @Test
    fun `should fail if target grade not found`() {
        val candidateId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val activePeriod = PromotionPeriod(UUID.randomUUID(), "Q1", LocalDate.now(), LocalDate.now(), PromotionPeriodStatus.OPEN)
        val candidate = User(candidateId, "dev@test.com", "Dev", null, managerId, false, false)

        `when`(promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)).thenReturn(Optional.of(activePeriod))
        `when`(userRepository.findById(candidateId)).thenReturn(Optional.of(candidate))
        `when`(gradeRepository.findById(any())).thenReturn(Optional.empty())

        val exception = shouldThrow<IllegalArgumentException> {
            promotionService.proposeCandidate(candidateId, managerId, UUID.randomUUID(), "Rationale")
        }
        exception.message?.contains("Target grade not found") shouldBe true
    }
}
