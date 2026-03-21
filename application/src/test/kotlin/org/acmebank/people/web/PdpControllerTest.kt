package org.acmebank.people.web

import org.acmebank.people.application.config.SecurityConfig
import org.acmebank.people.domain.PdpItem
import org.acmebank.people.domain.Pillar
import org.acmebank.people.domain.User
import org.acmebank.people.domain.port.PdpItemRepository
import org.acmebank.people.domain.port.UserRepository
import org.acmebank.people.domain.service.PdpService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.*

@WebMvcTest(PdpController::class)
@Import(SecurityConfig::class)
class PdpControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var pdpItemRepository: PdpItemRepository

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var pdpService: PdpService

    @Test
    @WithMockUser(username = "user@example.com")
    fun `GET pdp should display user and team pdps`() {
        val userId = UUID.randomUUID()
        val managerId = UUID.randomUUID()
        val user = User(userId, "user@example.com", "Developer Dave", null, managerId, false)
        val report = User(UUID.randomUUID(), "report@example.com", "Report", null, userId, false)

        val myPdp = PdpItem(UUID.randomUUID(), userId, UUID.randomUUID(), Pillar.THINKS, "Gap", "Plan", "Link", false, LocalDate.now(), LocalDate.now())
        val teamPdp = PdpItem(UUID.randomUUID(), report.id(), UUID.randomUUID(), Pillar.ENGAGES, "Gap2", "Plan2", "Link2", true, LocalDate.now(), LocalDate.now())

        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user))
        `when`(userRepository.findByManagerId(userId)).thenReturn(listOf(report))
        `when`(pdpItemRepository.findByUserId(userId)).thenReturn(listOf(myPdp))
        `when`(pdpItemRepository.findByUserId(report.id())).thenReturn(listOf(teamPdp))

        mockMvc.perform(get("/pdp"))
            .andExpect(status().isOk)
            .andExpect(view().name("pdp"))
            .andExpect(model().attributeExists("myPdps"))
            .andExpect(model().attributeExists("teamPdps"))
            .andExpect(model().attributeExists("userNames"))
    }

    @Test
    @WithMockUser(username = "user@example.com")
    fun `POST pdp complete should mark pdp as complete and redirect`() {
        val userId = UUID.randomUUID()
        val pdpId = UUID.randomUUID()
        val user = User(userId, "user@example.com", "Developer Dave", null, null, false)

        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user))

        mockMvc.perform(post("/pdp/{pdpId}/complete", pdpId).with(csrf()))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/pdp"))

        verify(pdpService).markAsCompleted(pdpId)
    }

    @Test
    @WithMockUser(username = "user@example.com")
    fun `POST create pdp should generate pdp item and redirect`() {
        val checkInId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val user = User(userId, "user@example.com", "Developer Dave", null, null, false)

        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user))

        mockMvc.perform(post("/pdp/checkin/{checkInId}/create", checkInId)
            .param("pillar", "DELIVERS")
            .param("gapDescription", "Manual gap definition")
            .param("actionablePlan", "Train XYZ")
            .param("userId", userId.toString())
            .with(csrf()))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/checkins/$checkInId"))

        verify(pdpService).createPdpItem(
            userId,
            checkInId,
            Pillar.DELIVERS,
            "Manual gap definition",
            "Train XYZ",
            "https://learning.acmebank.com/search?q=DELIVERS"
        )
    }
}
