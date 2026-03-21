package org.acmebank.people.application.adapter.out.framework

import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.acmebank.people.domain.Pillar
import org.acmebank.people.domain.port.PillarFrameworkService
import org.acmebank.people.application.adapter.out.framework.StaticPillarFrameworkService
import org.junit.jupiter.api.Test
import java.util.Optional

class PillarFrameworkServiceTest() {

    private val pillarFrameworkService: PillarFrameworkService = StaticPillarFrameworkService()

    @Test
    fun `should return definitions for all pillars`() {
        val definitions = pillarFrameworkService.getAllDefinitions()
        definitions.size shouldBe Pillar.entries.size
    }

    @Test
    fun `should return specific definition with full details`() {
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
