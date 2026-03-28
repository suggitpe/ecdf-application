package org.acmebank.people.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.PromotionPeriodRepository
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
class PromotionPeriodServiceTest {

    @Mock
    private lateinit var promotionPeriodRepository: PromotionPeriodRepository

    @InjectMocks
    private lateinit var promotionPeriodService: PromotionPeriodService

    @Test
    fun `should open a new promotion period`() {
        // Given
        val title = "Q1 2026 Promotions"
        val start = LocalDate.now()
        val end = LocalDate.now().plusMonths(1)
        
        `when`(promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)).thenReturn(Optional.empty())
        `when`(promotionPeriodRepository.save(any(PromotionPeriod::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val result = promotionPeriodService.openPeriod(title, start, end)

        // Then
        result.title shouldBe title
        result.status shouldBe PromotionPeriodStatus.OPEN
        result.startDate shouldBe start
        result.endDate shouldBe end
        verify(promotionPeriodRepository).save(any(PromotionPeriod::class.java))
    }

    @Test
    fun `should fail to open if a period is already open`() {
        // Given
        val existing = PromotionPeriod(UUID.randomUUID(), "Existing", LocalDate.now(), LocalDate.now(), PromotionPeriodStatus.OPEN)
        `when`(promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)).thenReturn(Optional.of(existing))

        // When & Then
        val exception = shouldThrow<IllegalStateException> {
            promotionPeriodService.openPeriod("New", LocalDate.now(), LocalDate.now())
        }
        exception.message shouldBe "Cannot open a new period while another is still OPEN."
    }

    @Test
    fun `should close a promotion period`() {
        // Given
        val periodId = UUID.randomUUID()
        val openPeriod = PromotionPeriod(periodId, "Open", LocalDate.now(), LocalDate.now(), PromotionPeriodStatus.OPEN)
        
        `when`(promotionPeriodRepository.findById(periodId)).thenReturn(Optional.of(openPeriod))
        `when`(promotionPeriodRepository.save(any(PromotionPeriod::class.java))).thenAnswer { it.getArgument(0) }

        // When
        val result = promotionPeriodService.closePeriod(periodId)

        // Then
        result.status shouldBe PromotionPeriodStatus.CLOSED
        verify(promotionPeriodRepository).save(any(PromotionPeriod::class.java))
    }

    @Test
    fun `should get active period`() {
        // Given
        val active = PromotionPeriod(UUID.randomUUID(), "Active", LocalDate.now(), LocalDate.now(), PromotionPeriodStatus.OPEN)
        `when`(promotionPeriodRepository.findByStatus(PromotionPeriodStatus.OPEN)).thenReturn(Optional.of(active))

        // When
        val result = promotionPeriodService.getActivePeriod()

        // Then
        result.isPresent shouldBe true
        result.get().title shouldBe "Active"
    }
}
