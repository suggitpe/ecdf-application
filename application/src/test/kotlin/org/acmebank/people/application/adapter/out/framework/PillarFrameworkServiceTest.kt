package org.acmebank.people.application.adapter.out.framework

import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.acmebank.people.domain.Pillar
import org.acmebank.people.domain.Score
import org.acmebank.people.domain.FrameworkPillar
import org.acmebank.people.domain.FrameworkLevel
import org.acmebank.people.domain.port.FrameworkRepository
import org.acmebank.people.domain.port.PillarFrameworkService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional
import java.util.UUID

class PillarFrameworkServiceTest {

    private val frameworkRepository: FrameworkRepository = mock(FrameworkRepository::class.java)
    private val pillarFrameworkService: PillarFrameworkService = DatabasePillarFrameworkService(frameworkRepository)

    private fun createStubPillar(): FrameworkPillar {
        val levels = (1..5).map {
            FrameworkLevel(UUID.randomUUID(), Pillar.THINKS, Score(it), "Level $it desc", "Example $it")
        }
        return FrameworkPillar(Pillar.THINKS, "Analytical Thinking", "Solves problems", levels)
    }

    @Test
    fun `should return definitions for all pillars`() {
        `when`(frameworkRepository.findAllPillars()).thenReturn(listOf(createStubPillar()))
        
        val definitions = pillarFrameworkService.getAllDefinitions()
        definitions.size shouldBe 1
    }

    @Test
    fun `should return specific definition with full details`() {
        `when`(frameworkRepository.findPillar(Pillar.THINKS)).thenReturn(Optional.of(createStubPillar()))
        
        val definition = pillarFrameworkService.getDefinition(Pillar.THINKS).orElse(null)
        definition.shouldNotBeNull()
        definition.title.shouldNotBeNull()
        definition.description.shouldNotBeNull()
        definition.levelDetails.size shouldBe 5
        
        val level3 = definition.levelDetails.first { it.level == 3 }
        level3.description.shouldNotBeNull()
        level3.examples.shouldNotBeNull()
    }
}
