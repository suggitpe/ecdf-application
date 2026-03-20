package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.CheckInEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.PdpItemEntity
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity
import org.acmebank.people.application.adapter.out.persistence.repository.CheckInJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.PdpItemJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class PdpItemRepositoryIntegrationTest {

    @Autowired
    private lateinit var pdpItemRepository: PdpItemJpaRepository

    @Autowired
    private lateinit var checkInRepository: CheckInJpaRepository

    @Autowired
    private lateinit var userRepository: UserJpaRepository

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save and find pdp item by user and check-in id`() {
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
            managerNotes = "Needs improvement in Leadership."
            status = "COMPLETED"
            checkInDate = LocalDate.now()
        }
        val savedCheckIn = checkInRepository.save(checkIn)

        val pdpItem = PdpItemEntity().apply {
            this.user = savedUser
            this.checkIn = savedCheckIn
            targetedPillar = "LEADERSHIP"
            gapDescription = "Lack of mentorship experience"
            actionablePlan = "Mentor a junior developer for 3 months"
            isCompleted = false
            createdDate = LocalDate.now()
            updatedDate = LocalDate.now()
        }

        // When
        val savedPdpItem = pdpItemRepository.save(pdpItem)
        val foundByUser = pdpItemRepository.findByUserId(savedUser.id)
        val foundByCheckIn = pdpItemRepository.findByCheckInId(savedCheckIn.id)

        // Then
        assertThat(savedPdpItem.id).isNotNull()
        assertThat(foundByUser).hasSize(1)
        assertThat(foundByCheckIn).hasSize(1)
        assertThat(foundByCheckIn[0].targetedPillar).isEqualTo("LEADERSHIP")
        assertThat(foundByCheckIn[0].actionablePlan).isEqualTo("Mentor a junior developer for 3 months")
    }
}
