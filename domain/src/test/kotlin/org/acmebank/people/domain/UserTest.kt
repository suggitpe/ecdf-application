package org.acmebank.people.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class UserTest {

    @Test
    fun `user should have an expected pillar level per grade requirement`() {
        val directorExpectations = Pillar.values().associateWith { Score(4) }
        val directorGrade = Grade(UUID.randomUUID(), "Director", "Management", directorExpectations)
        val directorUser = User(UUID.randomUUID(), "director@example.com", "Director Dave", directorGrade, null, false, false)

        val vpExpectations = Pillar.values().associateWith { Score(3) }
        val vpGrade = Grade(UUID.randomUUID(), "Vice President", "Management", vpExpectations)
        val vpUser = User(UUID.randomUUID(), "vp@example.com", "VP Vera", vpGrade, null, false, false)

        directorUser.getExpectedPillarLevel(Pillar.THINKS).value() shouldBe 4
        directorUser.getExpectedPillarLevel(Pillar.DELIVERS).value() shouldBe 4

        vpUser.getExpectedPillarLevel(Pillar.THINKS).value() shouldBe 3
        vpUser.getExpectedPillarLevel(Pillar.DELIVERS).value() shouldBe 3
    }
}
