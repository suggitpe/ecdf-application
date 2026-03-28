package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.AssessmentRepository
import org.acmebank.people.domain.port.EvidenceRepository
import org.acmebank.people.domain.port.UserRepository
import org.acmebank.people.domain.service.AssessmentService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
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

@WebMvcTest(AssessmentController::class)
class AssessmentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var assessmentRepository: AssessmentRepository

    @MockitoBean
    private lateinit var evidenceRepository: EvidenceRepository

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var assessmentService: AssessmentService

    private val evidenceId = UUID.randomUUID()
    private val assessorId = UUID.randomUUID()
    private val userId = UUID.randomUUID()

    private val mockAssessor = User(
        assessorId, "mgr@example.com", "Manager Bob",
        Grade(UUID.randomUUID(), "Manager", "Engineering", emptyMap()),
        null, true, false
    )

    private val mockEvidence = Evidence(
        evidenceId, userId, "Project X Refactor", "Description", "Impact X", "Complexity Y", "Contribution Z",
        mapOf(Pillar.DESIGNS to EvidenceRating(Score(3), "")), emptyList(), emptyList(), 
        EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
    )

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show assessment form for submitted evidence`() {
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(mockEvidence))
        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockAssessor))
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(User(userId, "dev@example.com", "Jane Dev", null, null, false, false)))

        mockMvc.perform(get("/assessment/$evidenceId"))
            .andExpect(status().isOk)
            .andExpect(view().name("assessment-form"))
            .andExpect(model().attributeExists("evidence", "developer", "pillars"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should submit assessment and redirect to queue`() {
        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockAssessor))
        `when`(assessmentService.submitAssessment(any(), any(), any(), any()))
            .thenReturn(Assessment(UUID.randomUUID(), evidenceId, assessorId, emptyMap(), "Good", false, LocalDate.now()))

        mockMvc.perform(
            post("/assessment/$evidenceId")
                .param("reviewSummary", "Great work on the refactor!")
                .param("pillars", "DESIGNS")
                .param("scores[DESIGNS]", "4")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/assessment/queue"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should assign third party assessor and redirect`() {
        val itaId = UUID.randomUUID()
        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockAssessor))
        `when`(assessmentService.assignThirdPartyAssessor(any(), any()))
            .thenReturn(Assessment(UUID.randomUUID(), evidenceId, itaId, emptyMap(), null, true, null))

        mockMvc.perform(
            post("/assessment/$evidenceId/assign")
                .param("assessorId", itaId.toString())
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/assessment/queue"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show assessor queue view with reports and assignments`() {
        val reporterId = UUID.randomUUID()
        val reporter = User(reporterId, "dev@example.com", "Reporter", null, null, false, false)
        val teamEvidence = Evidence(
            UUID.randomUUID(), reporterId, "Team Project", "Description", "Impact", "Complex", "Contrib",
            emptyMap(), emptyList(), emptyList(), EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )

        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockAssessor))
        `when`(userRepository.findByManagerId(assessorId)).thenReturn(listOf(reporter))
        `when`(evidenceRepository.findByUserIdAndStatus(reporterId, EvidenceStatus.SUBMITTED)).thenReturn(listOf(teamEvidence))
        `when`(assessmentRepository.findByAssessorId(assessorId)).thenReturn(emptyList())

        mockMvc.perform(get("/assessment/queue"))
            .andExpect(status().isOk)
            .andExpect(view().name("assessor-queue"))
            .andExpect(model().attributeExists("teamEvidence"))
            .andExpect(model().attributeExists("pendingAssessments"))
    }
}
