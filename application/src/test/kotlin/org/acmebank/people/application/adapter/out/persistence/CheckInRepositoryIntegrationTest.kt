package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.CheckInEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity
import org.acmebank.people.application.adapter.out.persistence.repository.CheckInJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class CheckInRepositoryIntegrationTest {

    @Autowired
    private lateinit var checkInRepository: CheckInJpaRepository

    @Autowired
    private lateinit var userRepository: UserJpaRepository

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save and find check-in by user id`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        }
        val savedGrade = gradeRepository.save(grade)

        val user = UserEntity().apply {
            email = "dev@acmebank.com"
            fullName = "Jane Dev"
            isIta = false
            this.grade = savedGrade
        }
        val savedUser = userRepository.save(user)

        val manager = UserEntity().apply {
            email = "mgr@acmebank.com"
            fullName = "Jane Manager"
            isIta = true
            this.grade = savedGrade
        }
        val savedManager = userRepository.save(manager)

        val checkIn = CheckInEntity().apply {
            this.user = savedUser
            this.manager = savedManager
            periodStartDate = LocalDate.of(2023, 1, 1)
            periodEndDate = LocalDate.of(2023, 3, 31)
            managerNotes = "Good progress this quarter."
            status = "DRAFT"
            checkInDate = LocalDate.now()
        }

        // When
        val savedCheckIn = checkInRepository.save(checkIn)
        val foundCheckIns = checkInRepository.findByUserId(savedUser.id)

        // Then
        savedCheckIn.id shouldNotBe null
        foundCheckIns shouldHaveSize 1
        foundCheckIns[0].managerNotes shouldBe "Good progress this quarter."
        foundCheckIns[0].periodStartDate shouldBe LocalDate.of(2023, 1, 1)
    }
}
