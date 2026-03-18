package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeExpectationEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeExpectationId
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository
import org.acmebank.people.domain.Pillar
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.optional.shouldBePresent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class GradeRepositoryIntegrationTest {

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save grade and its expectations`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        }

        val expectation = GradeExpectationEntity().apply {
            expectedScore = 3
            this.grade = grade
        }
        
        val savedGrade = gradeRepository.save(grade)
        
        expectation.id = GradeExpectationId(savedGrade.id, Pillar.DESIGNS.name)
        savedGrade.expectations.add(expectation)
        val finalGrade = gradeRepository.save(savedGrade)

        // When
        val foundGrade = gradeRepository.findById(finalGrade.id)

        // Then
        foundGrade.shouldBePresent()
        foundGrade.get().name shouldBe "Senior"
        foundGrade.get().expectations shouldHaveSize 1
        foundGrade.get().expectations[0].expectedScore shouldBe 3
        foundGrade.get().expectations[0].id.pillar shouldBe Pillar.DESIGNS.name
    }

    @Test
    fun `should find grade by name and role`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Staff"
            role = "Software Engineer"
        }
        gradeRepository.save(grade)

        // When
        val foundGrade = gradeRepository.findByNameAndRole("Staff", "Software Engineer")

        // Then
        foundGrade.shouldBePresent()
        foundGrade.get().name shouldBe "Staff"
    }
}
