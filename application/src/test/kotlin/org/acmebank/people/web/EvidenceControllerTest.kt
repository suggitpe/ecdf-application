package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.acmebank.people.domain.Evidence
import org.acmebank.people.domain.EvidenceStatus
import org.acmebank.people.domain.Pillar
import org.acmebank.people.domain.Score
import org.acmebank.people.domain.EvidenceRating
import org.acmebank.people.domain.User
import org.acmebank.people.domain.Grade
import org.acmebank.people.domain.port.UserRepository
import org.acmebank.people.domain.port.EvidenceRepository
import org.acmebank.people.domain.service.EvidenceService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

@WebMvcTest(EvidenceController::class)
@Import(ThymeleafAutoConfiguration::class)
class EvidenceControllerTest {

    companion object {
        @TempDir
        @JvmStatic
        lateinit var tempDir: Path

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.storage.path") { tempDir.toAbsolutePath().toString() }
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var evidenceRepository: EvidenceRepository

    @MockitoBean
    private lateinit var evidenceService: EvidenceService

    private val userId = UUID.randomUUID()
    private val evidenceId = UUID.randomUUID()

    private val mockUser = User(
        userId, "user@example.com", "Engineer Bob",
        Grade(UUID.randomUUID(), "Software Engineer", "Engineering", emptyMap()),
        null, false
    )

    private val mockEvidence = Evidence(
        evidenceId, userId, "Project X Refactor", "Description of refactor", "High Impact", "Complex", "Led effort",
        mapOf(Pillar.DESIGNS to EvidenceRating(Score(3), ""), Pillar.DELIVERS to EvidenceRating(Score(4), "")),
        emptyList(), emptyList(), EvidenceStatus.DRAFT, LocalDate.now(), LocalDate.now()
    )

    // -------------------------------------------------------------------------
    // GET /evidence — list
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should return evidence list view and populate model on success`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findByUserId(userId)).thenReturn(listOf(mockEvidence))

        mockMvc.perform(get("/evidence"))
            .andExpect(status().isOk)
            .andExpect(view().name("evidence-list"))
            .andExpect(model().attributeExists("evidenceList"))
            .andDo { result ->
                val modelAndView = result.modelAndView
                modelAndView.shouldNotBeNull()
                modelAndView.model["evidenceList"] shouldBe listOf(mockEvidence)
            }
    }

    // -------------------------------------------------------------------------
    // GET /evidence/new — show create form
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should show create evidence form with pillars in model`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))

        mockMvc.perform(get("/evidence/new"))
            .andExpect(status().isOk)
            .andExpect(view().name("evidence-form"))
            .andExpect(model().attributeExists("pillars", "currentUser"))
    }

    // -------------------------------------------------------------------------
    // POST /evidence/new — submit create form
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should create evidence from multipart form and redirect to list`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(evidenceService.createEvidence(any(UUID::class.java), any(String::class.java))).thenReturn(mockEvidence)
        `when`(evidenceService.updateEvidence(any(UUID::class.java), any(String::class.java), any(String::class.java), any(String::class.java), any(String::class.java), any(String::class.java), any())).thenReturn(mockEvidence)

        val attachment = MockMultipartFile(
            "attachment", "evidence.txt", MediaType.TEXT_PLAIN_VALUE, "some content".toByteArray()
        )

        mockMvc.perform(
            multipart("/evidence/new")
                .file(attachment)
                .param("title", "Project X Refactor")
                .param("description", "Description of refactor")
                .param("impact", "High Impact")
                .param("complexity", "Complex")
                .param("contribution", "Led effort")
                .param("pillars", "DESIGNS", "DELIVERS")
                .param("scores[DESIGNS]", "3")
                .param("scores[DELIVERS]", "4")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/evidence"))
    }

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should reject create form when title is blank`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))

        mockMvc.perform(
            multipart("/evidence/new")
                .param("title", "")
                .param("description", "")
                .param("impact", "Some impact")
                .param("complexity", "Low")
                .param("contribution", "Helped")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(view().name("evidence-form"))
    }

    // -------------------------------------------------------------------------
    // GET /evidence/{id} — view single evidence
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should show evidence detail view`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(mockEvidence))

        mockMvc.perform(get("/evidence/$evidenceId"))
            .andExpect(status().isOk)
            .andExpect(view().name("evidence-detail"))
            .andExpect(model().attributeExists("evidence", "currentUser", "isManager"))
    }

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should return 404 when evidence not found`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(any(UUID::class.java))).thenReturn(Optional.empty())

        mockMvc.perform(get("/evidence/$evidenceId"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should allow manager to view evidence and see ITA list`() {
        val managerId = UUID.randomUUID()
        val mockManager = User(managerId, "mgr@example.com", "Manager Bob", mockUser.grade(), null, false)
        val mockUserWithManager = User(mockUser.id(), mockUser.email(), mockUser.fullName(), mockUser.grade(), managerId, mockUser.isIta())
        val mockIta = User(UUID.randomUUID(), "ita@example.com", "ITA Alice", mockUser.grade(), null, true)

        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockManager))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(mockEvidence))
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUserWithManager))
        `when`(userRepository.findItas()).thenReturn(listOf(mockIta))

        mockMvc.perform(get("/evidence/$evidenceId"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("isManager", true))
            .andExpect(model().attributeExists("allItas"))
    }

    // -------------------------------------------------------------------------
    // GET /evidence/{id}/edit — show edit form
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should show edit form for DRAFT evidence`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(mockEvidence))

        mockMvc.perform(get("/evidence/$evidenceId/edit"))
            .andExpect(status().isOk)
            .andExpect(view().name("evidence-form"))
            .andExpect(model().attributeExists("evidence", "pillars"))
    }

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should redirect when trying to edit non-DRAFT evidence`() {
        val submittedEvidence = Evidence(
            evidenceId, userId, "Project X Refactor", "Description", "High Impact", "Complex", "Led effort",
            mapOf(Pillar.DESIGNS to EvidenceRating(Score(3), "")), emptyList(), emptyList(),
            EvidenceStatus.SUBMITTED, LocalDate.now(), LocalDate.now()
        )
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(submittedEvidence))

        mockMvc.perform(get("/evidence/$evidenceId/edit"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/evidence"))
    }

    // -------------------------------------------------------------------------
    // POST /evidence/{id}/edit — submit edit form
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should update DRAFT evidence and redirect to list`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(mockEvidence))
        `when`(evidenceService.updateEvidence(any(), any(), any(), any(), any(), any(), any())).thenReturn(mockEvidence)

        val attachment = MockMultipartFile(
            "attachment", "", MediaType.TEXT_PLAIN_VALUE, ByteArray(0)
        )

        // POST to edit endpoint using HTTP method override since multipart uses POST
        mockMvc.perform(
            multipart("/evidence/$evidenceId/edit")
                .file(attachment)
                .param("title", "Updated Title")
                .param("description", "Updated Description")
                .param("impact", "Updated Impact")
                .param("complexity", "Low")
                .param("contribution", "Pair programmed")
                .param("pillars", "DESIGNS")
                .param("scores[DESIGNS]", "2")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/evidence"))
    }

    // -------------------------------------------------------------------------
    // GET /evidence/{id}/attachment — secure file download
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should return 404 for download when evidence has no attachments`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(mockEvidence))

        mockMvc.perform(get("/evidence/$evidenceId/attachment/0"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should download attachment when user has permission`() {
        val fileName = "test-file.txt"
        val fullPath = tempDir.resolve(fileName)
        java.nio.file.Files.write(fullPath, "hello world".toByteArray())
        
        val evidenceWithAttachment = Evidence(
            mockEvidence.id(), mockEvidence.userId(), mockEvidence.title(), mockEvidence.description(), mockEvidence.impact(),
            mockEvidence.complexity(), mockEvidence.contribution(), mockEvidence.selfAssessment(),
            mockEvidence.links(), listOf(fullPath.toString()), mockEvidence.status(),
            mockEvidence.createdDate(), mockEvidence.lastModifiedDate()
        )
        
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(evidenceWithAttachment))

        mockMvc.perform(get("/evidence/$evidenceId/attachment/0"))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\""))
    }

    // -------------------------------------------------------------------------
    // POST /evidence/{id}/submit — submit for review
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should submit DRAFT evidence and redirect to list`() {
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))
        `when`(evidenceRepository.findById(evidenceId)).thenReturn(Optional.of(mockEvidence))
        `when`(evidenceService.submitEvidence(evidenceId)).thenReturn(mockEvidence)

        mockMvc.perform(
            post("/evidence/$evidenceId/submit")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/evidence"))
    }
}
