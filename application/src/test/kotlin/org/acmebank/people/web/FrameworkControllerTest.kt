package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import org.acmebank.people.domain.Grade
import org.acmebank.people.domain.Pillar
import org.acmebank.people.domain.Score
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import org.mockito.ArgumentMatchers.any

@WebMvcTest(FrameworkController::class)
class FrameworkControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var gradeRepository: GradeRepository

    @MockitoBean
    private lateinit var pillarFrameworkService: PillarFrameworkService

    @Test
    @WithMockUser(username = "jane.doe@example.com")
    fun `should show framework list page`() {
        val grade1 = Grade(UUID.randomUUID(), "Level 1", "Engineering", emptyMap())
        val grade2 = Grade(UUID.randomUUID(), "Level 2", "Engineering", emptyMap())
        
        `when`(gradeRepository.findAll()).thenReturn(listOf(grade1, grade2))

        mockMvc.perform(get("/framework"))
            .andExpect(status().isOk)
            .andExpect(view().name("framework"))
            .andExpect(model().attributeExists("grades"))
    }

    @Test
    @WithMockUser(username = "jane.doe@example.com")
    fun `should show specific grade details`() {
        val gradeId = UUID.randomUUID()
        val expectations = Pillar.entries.associateWith { Score(3) }
        val grade = Grade(gradeId, "Level 1", "Engineering", expectations)
        
        `when`(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade))
        `when`(pillarFrameworkService.getAllDefinitions()).thenReturn(emptyList())

        mockMvc.perform(get("/framework/$gradeId"))
            .andExpect(status().isOk)
            .andExpect(view().name("framework-detail"))
            .andExpect(model().attribute("grade", grade))
            .andExpect(model().attributeExists("pillarDefinitions"))
    }
    
    @Test
    @WithMockUser(username = "jane.doe@example.com")
    fun `should return 404 for non-existent grade`() {
        `when`(gradeRepository.findById(any())).thenReturn(Optional.empty())

        mockMvc.perform(get("/framework/${UUID.randomUUID()}"))
            .andExpect(status().isNotFound)
    }
}
