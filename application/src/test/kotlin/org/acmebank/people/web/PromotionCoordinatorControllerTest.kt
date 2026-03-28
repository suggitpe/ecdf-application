package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.*
import org.acmebank.people.domain.service.PromotionService
import org.acmebank.people.domain.service.PromotionPeriodService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.*

@WebMvcTest(PromotionCoordinatorController::class)
class PromotionCoordinatorControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var promotionPeriodService: PromotionPeriodService

    @MockitoBean
    private lateinit var promotionService: PromotionService

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var gradeRepository: GradeRepository

    @Test
    @WithMockUser(roles = ["PROMOTION_COORDINATOR"])
    fun `should list periods and active cases`() {
        `when`(promotionPeriodService.getAllPeriods()).thenReturn(emptyList())
        `when`(promotionPeriodService.getActivePeriod()).thenReturn(Optional.empty())
        `when`(promotionService.getActiveCases()).thenReturn(emptyList())
        `when`(userRepository.findAll()).thenReturn(emptyList())
        `when`(gradeRepository.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/promotion/coordinator/periods"))
            .andExpect(status().isOk)
            .andExpect(view().name("promotion-periods"))
            .andExpect(model().attributeExists("periods", "activeCases", "userMap", "gradeMap"))
    }

    @Test
    @WithMockUser(roles = ["PROMOTION_COORDINATOR"])
    fun `should open period and redirect`() {
        mockMvc.perform(
            post("/promotion/coordinator/periods/open")
                .param("title", "Q1")
                .param("startDate", "2026-01-01")
                .param("endDate", "2026-03-31")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/promotion/coordinator/periods"))
    }

    @Test
    @WithMockUser(roles = ["PROMOTION_COORDINATOR"])
    fun `should close period and redirect`() {
        mockMvc.perform(
            post("/promotion/coordinator/periods/close")
                .param("periodId", UUID.randomUUID().toString())
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/promotion/coordinator/periods"))
    }
}
