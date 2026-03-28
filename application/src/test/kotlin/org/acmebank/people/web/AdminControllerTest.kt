package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.GradeRepository
import org.acmebank.people.domain.port.PillarFrameworkService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

import org.springframework.context.annotation.Import
import org.acmebank.people.application.config.SecurityConfig

@WebMvcTest(AdminController::class)
@Import(SecurityConfig::class)
class AdminControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var gradeRepository: GradeRepository

    @MockitoBean
    private lateinit var pillarFrameworkService: PillarFrameworkService

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should show admin framework page`() {
        val definitions = listOf(
            PillarDefinition(Pillar.THINKS, "Thinks", "Desc", emptyList())
        )
        `when`(pillarFrameworkService.getAllDefinitions()).thenReturn(definitions)

        mockMvc.perform(get("/admin/framework"))
            .andExpect(status().isOk)
            .andExpect(view().name("admin-framework"))
            .andExpect(model().attribute("pillarDefinitions", definitions))
    }

    @Test
    @WithMockUser(roles = ["USER"]) // Regular user should be denied
    fun `should deny access to admin framework page for regular users`() {
        mockMvc.perform(get("/admin/framework"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should show admin roles page`() {
        val grade1 = Grade(UUID.randomUUID(), "Level 1", "Engineering", emptyMap())
        `when`(gradeRepository.findAll()).thenReturn(listOf(grade1))

        mockMvc.perform(get("/admin/roles"))
            .andExpect(status().isOk)
            .andExpect(view().name("admin-roles"))
            .andExpect(model().attributeExists("grades"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should update pillar definition`() {
        mockMvc.perform(post("/admin/framework")
            .with(csrf())
            .param("pillar", "THINKS")
            .param("title", "Updated Thinks")
            .param("description", "Updated Desc"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/admin/framework"))

        verify(pillarFrameworkService).updateDefinition(any())
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should update role expectations`() {
        val gradeId = UUID.randomUUID()
        mockMvc.perform(post("/admin/roles")
            .with(csrf())
            .param("gradeId", gradeId.toString())
            .param("THINKS", "4")
            .param("ENGAGES", "3"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/admin/roles"))

        verify(gradeRepository).updateExpectations(any(), any())
    }
}
