package org.acmebank.people.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class AssessmentTest {

    @Test
    fun `should create valid assessment`() {
        val scores = mapOf(Pillar.DELIVERS to Score(4))
        val assessment = Assessment(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            scores,
            "Good job",
            true,
            LocalDate.now()
        )
        assessment.reviewSummary shouldBe "Good job"
        assessment.isThirdParty shouldBe true
    }

    @Test
    fun `should throw exception if review summary is blank`() {
        val scores = mapOf(Pillar.DELIVERS to Score(4))
        shouldThrow<IllegalArgumentException> {
            Assessment(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                scores,
                "  ",
                true,
                LocalDate.now()
            )
        }.message shouldBe "Review summary must not be blank"
    }
    
    @Test
    fun `should throw exception if assessed scores is empty`() {
        shouldThrow<IllegalArgumentException> {
            Assessment(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                emptyMap(),
                "Good job",
                true,
                LocalDate.now()
            )
        }.message shouldBe "Assessed scores must not be empty"
    }
}
