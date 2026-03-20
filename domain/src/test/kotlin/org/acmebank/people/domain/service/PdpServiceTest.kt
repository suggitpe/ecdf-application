package org.acmebank.people.domain.service

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.collections.shouldHaveSize
import org.acmebank.people.domain.*
import org.acmebank.people.domain.PillarScoreInfo
import org.acmebank.people.domain.port.PdpItemRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class PdpServiceTest {

    @Mock
    private lateinit var pdpItemRepository: PdpItemRepository

    @InjectMocks
    private lateinit var pdpService: PdpService

    @Test
    fun `should create a single PDP item`() {
        val userId = UUID.randomUUID()
        val checkInId = UUID.randomUUID()
        val pillar = Pillar.THINKS
        
        `when`(pdpItemRepository.save(any(PdpItem::class.java))).thenAnswer { it.arguments[0] }

        val pdpItem = pdpService.createPdpItem(userId, checkInId, pillar, "Gap description", "Actionable plan", "http://learning.journey")

        pdpItem.userId shouldBe userId
        pdpItem.checkInId shouldBe checkInId
        pdpItem.targetedPillar shouldBe pillar
        pdpItem.gapDescription shouldBe "Gap description"
        pdpItem.actionablePlan shouldBe "Actionable plan"
        pdpItem.learningJourneyLink shouldBe "http://learning.journey"
        pdpItem.isCompleted shouldBe false
        
        verify(pdpItemRepository).save(any(PdpItem::class.java))
    }

    @Test
    fun `should automatically create mandatory PDPs for underperforming pillars in a CheckIn`() {
        val userId = UUID.randomUUID()
        val checkInId = UUID.randomUUID()
        
        val currentGrade = Grade(UUID.randomUUID(), "Senior", "Developer", mapOf(
            Pillar.THINKS to Score(4),
            Pillar.DELIVERS to Score(4),
            Pillar.ENGAGES to Score(3)
        ))

        val checkInScores = mapOf(
            Pillar.THINKS to PillarScoreInfo(Score(3), UUID.randomUUID()),   // Underperforming (3 < 4)
            Pillar.DELIVERS to PillarScoreInfo(Score(4), UUID.randomUUID())  // On Track (4 == 4)
            // ENGAGES is missing, defaults to null, meaning underperforming
        )

        val checkIn = CheckIn(
            checkInId, userId, UUID.randomUUID(), 
            checkInScores, "Notes", CheckInStatus.UNDERPERFORMING, LocalDate.now()
        )

        `when`(pdpItemRepository.save(any(PdpItem::class.java))).thenAnswer { it.arguments[0] }

        val pdpItems = pdpService.autoGenerateMandatoryPdps(checkIn, currentGrade)

        pdpItems.size shouldBe 2
        
        val thinksPdp = pdpItems.find { it.targetedPillar == Pillar.THINKS }
        thinksPdp shouldNotBe null
        thinksPdp!!.gapDescription.contains("Below expectation") shouldBe true
        thinksPdp.userId shouldBe userId
        thinksPdp.checkInId shouldBe checkInId

        val engagesPdp = pdpItems.find { it.targetedPillar == Pillar.ENGAGES }
        engagesPdp shouldNotBe null
        
        verify(pdpItemRepository, times(2)).save(any(PdpItem::class.java))
    }

    @Test
    fun `should complete a pdp item`() {
        val id = UUID.randomUUID()
        val item = PdpItem(
            id, UUID.randomUUID(), UUID.randomUUID(), Pillar.THINKS, 
            "Gap", "Plan", "Link", false, LocalDate.now(), LocalDate.now()
        )
        
        `when`(pdpItemRepository.findById(id)).thenReturn(Optional.of(item))
        `when`(pdpItemRepository.save(any(PdpItem::class.java))).thenAnswer { it.arguments[0] }

        val completed = pdpService.markAsCompleted(id)

        completed.isCompleted shouldBe true
        verify(pdpItemRepository).findById(id)
        verify(pdpItemRepository).save(any(PdpItem::class.java))
    }
}
