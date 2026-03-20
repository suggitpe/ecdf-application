package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.acmebank.people.domain.CheckIn
import org.acmebank.people.domain.PillarScoreInfo
import org.acmebank.people.domain.CheckInStatus
import org.acmebank.people.domain.Evidence
import org.acmebank.people.domain.EvidenceStatus
import org.acmebank.people.domain.Grade
import org.acmebank.people.domain.Pillar
import org.acmebank.people.domain.Score
import org.acmebank.people.domain.EvidenceRating
import org.acmebank.people.domain.User
import org.acmebank.people.domain.port.CheckInRepository
import org.acmebank.people.domain.port.EvidenceRepository
import org.acmebank.people.domain.port.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

@WebMvcTest(DashboardController::class)
class DashboardControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var checkInRepository: CheckInRepository

    @MockitoBean
    private lateinit var evidenceRepository: EvidenceRepository

    @Test
    @WithMockUser(username = "jane.doe@example.com")
    fun `should return dashboard view and populate model on success`() {
        val userId = UUID.randomUUID()
        val mockUser = User(
            userId, "jane.doe@example.com", "Jane Doe",
            Grade(UUID.randomUUID(), "Software Engineer", "Software Engineer", emptyMap()),
            null, false
        )
        val mockCheckIn = CheckIn(
            UUID.randomUUID(),
            userId,
            UUID.randomUUID(), // managerId
            mapOf(Pillar.DESIGNS to PillarScoreInfo(Score(3), UUID.randomUUID())),
            "Good progress",
            CheckInStatus.ON_TRACK,
            LocalDate.now()
        )
        val mockEvidence = Evidence(
            UUID.randomUUID(), userId, "Refactored Core System", "Description of work done", "High impact", "Complex", "Led the effort",
            mapOf(Pillar.DESIGNS to EvidenceRating(Score(3), "")),
            emptyList(), emptyList(), EvidenceStatus.DRAFT, LocalDate.now(), LocalDate.now()
        )

        `when`(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(mockUser))
        `when`(checkInRepository.findByUserId(userId)).thenReturn(listOf(mockCheckIn))
        `when`(evidenceRepository.findByUserId(userId)).thenReturn(listOf(mockEvidence))

        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isOk)
            .andExpect(view().name("dashboard"))
            .andExpect(model().attributeExists("user", "latestCheckIn", "historicalCheckIns", "recentEvidence", "radarLabels", "radarData", "pillars"))
            .andDo { result ->
                val modelAndView = result.modelAndView
                modelAndView.shouldNotBeNull()
                val model = modelAndView.model
                model["user"] shouldBe mockUser
                model["latestCheckIn"] shouldBe mockCheckIn
                model["historicalCheckIns"] shouldBe listOf(mockCheckIn)
                model["recentEvidence"] shouldBe listOf(mockEvidence)
            }
    }

    @Test
    @WithMockUser(username = "unknown@example.com")
    fun `should handle missing user by returning error or empty dashboard`() {
        `when`(userRepository.findByEmail(any())).thenReturn(Optional.empty())

        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isNotFound)
    }
}
