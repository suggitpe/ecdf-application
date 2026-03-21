package org.acmebank.people.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class GradeTest {

    @Test
    fun `Grade should return the score specified in the expectations map`() {
        // Given
        val expectations = mutableMapOf<Pillar, Score>()
        expectations[Pillar.THINKS] = Score(4)
        expectations[Pillar.ENGAGES] = Score(3)
        // When
        val grade = Grade(UUID.randomUUID(), "Software Engineer", "Engineering", expectations)

        // Then
        grade.getExpectationFor(Pillar.THINKS).value() shouldBe 4
        grade.getExpectationFor(Pillar.ENGAGES).value() shouldBe 3
    }

    @Test
    fun `Grade should return 1 when looking up an unexpected pillar`() {
        // Given
        val expectations = mutableMapOf<Pillar, Score>()

        // When
        val grade = Grade(UUID.randomUUID(), "Designer", "UX", expectations)

        // Then
        grade.getExpectationFor(Pillar.DESIGNS).value() shouldBe 1
    }
}
