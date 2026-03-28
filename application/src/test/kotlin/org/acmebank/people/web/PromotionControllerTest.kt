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

@WebMvcTest(PromotionController::class)
class PromotionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var promotionService: PromotionService

    @MockitoBean
    private lateinit var promotionPeriodService: PromotionPeriodService

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var gradeRepository: GradeRepository

    private val managerId = UUID.randomUUID()
    private val candidateId = UUID.randomUUID()
    private val targetGradeId = UUID.randomUUID()
    private val periodId = UUID.randomUUID()

    private val mockManager = User(managerId, "mgr@acmebank.org", "Manager Mary", null, null, true, false)
    private val mockCandidate = User(candidateId, "dev@acmebank.org", "Developer Dave", null, managerId, false, false)
    private val mockGrade = Grade(targetGradeId, "Senior", "Software Engineer", emptyMap())
    private val mockPeriod = PromotionPeriod(periodId, "Q1 2026", LocalDate.now(), LocalDate.now().plusMonths(1), PromotionPeriodStatus.OPEN)

    @Test
    @WithMockUser(username = "mgr@acmebank.org")
    fun `should show promotion propose form for direct report`() {
        `when`(userRepository.findByEmail("mgr@acmebank.org")).thenReturn(Optional.of(mockManager))
        `when`(userRepository.findById(candidateId)).thenReturn(Optional.of(mockCandidate))
        `when`(gradeRepository.findAll()).thenReturn(listOf(mockGrade))
        `when`(promotionPeriodService.getActivePeriod()).thenReturn(Optional.of(mockPeriod))

        mockMvc.perform(get("/promotion/propose").param("candidateId", candidateId.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("promotion-propose"))
            .andExpect(model().attributeExists("candidate", "grades", "activePeriod"))
    }

    @Test
    @WithMockUser(username = "mgr@acmebank.org")
    fun `should propose candidate and redirect to dashboard`() {
        val mockCase = PromotionCase(UUID.randomUUID(), candidateId, managerId, targetGradeId, periodId, "Rationale", PromotionStatus.PROPOSED)
        
        `when`(userRepository.findByEmail("mgr@acmebank.org")).thenReturn(Optional.of(mockManager))
        `when`(promotionService.proposeCandidate(any(), any(), any(), any())).thenReturn(mockCase)

        mockMvc.perform(
            post("/promotion/propose")
                .param("candidateId", candidateId.toString())
                .param("targetGradeId", targetGradeId.toString())
                .param("rationale", "Excellent growth.")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
    }
}
