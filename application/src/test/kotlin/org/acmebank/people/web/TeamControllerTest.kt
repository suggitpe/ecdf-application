package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.acmebank.people.domain.CheckIn
import org.acmebank.people.domain.CheckInStatus
import org.acmebank.people.domain.Grade
import org.acmebank.people.domain.Pillar
import org.acmebank.people.domain.Score
import org.acmebank.people.domain.User
import org.acmebank.people.domain.port.CheckInRepository
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

@WebMvcTest(TeamController::class)
class TeamControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var checkInRepository: CheckInRepository

    @Test
    @WithMockUser(username = "manager@example.com")
    fun `should return team view and populate model on success`() {
        val managerId = UUID.randomUUID()
        val mockManager = User(
            managerId, "manager@example.com", "Manager Alice",
            Grade(UUID.randomUUID(), "Engineering Manager", "Management", emptyMap()),
            null, true
        )

        val teamMemberId = UUID.randomUUID()
        val mockTeamMember = User(
            teamMemberId, "user@example.com", "Engineer Bob",
            Grade(UUID.randomUUID(), "Software Engineer", "Engineering", emptyMap()),
            managerId, false
        )

        val mockCheckIn = CheckIn(
            UUID.randomUUID(),
            teamMemberId,
            UUID.randomUUID(),
            mapOf(Pillar.DESIGNS to Score(3)),
            "Good progress",
            CheckInStatus.ON_TRACK,
            LocalDate.now()
        )

        `when`(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(mockManager))
        `when`(userRepository.findByManagerId(managerId)).thenReturn(listOf(mockTeamMember))
        `when`(checkInRepository.findByUserId(teamMemberId)).thenReturn(listOf(mockCheckIn))

        mockMvc.perform(get("/team"))
            .andExpect(status().isOk)
            .andExpect(view().name("team"))
            .andExpect(model().attributeExists("manager", "teamMembers"))
            .andDo { result ->
                val modelAndView = result.modelAndView
                modelAndView.shouldNotBeNull()
                val model = modelAndView.model
                model["manager"] shouldBe mockManager
            }
    }

    @Test
    @WithMockUser(username = "unknown@example.com")
    fun `should handle missing manager`() {
        `when`(userRepository.findByEmail(any())).thenReturn(Optional.empty())

        mockMvc.perform(get("/team"))
            .andExpect(status().isNotFound)
    }
}
