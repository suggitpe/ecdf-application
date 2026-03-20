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
import io.kotest.matchers.maps.shouldHaveSize as shouldHaveMapSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

import org.acmebank.people.application.adapter.out.persistence.repository.EvidenceJpaRepository
import org.acmebank.people.application.adapter.out.persistence.entity.EvidenceEntity
import java.util.UUID

@DataJpaTest
@ActiveProfiles("test")
class CheckInRepositoryIntegrationTest {

    @Autowired
    private lateinit var checkInRepository: CheckInJpaRepository

    @Autowired
    private lateinit var userRepository: UserJpaRepository

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Autowired
    private lateinit var evidenceJpaRepository: EvidenceJpaRepository

    @Test
    fun `should save and find check-in with holistic scores and evidence link`() {
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

        val evidence = EvidenceEntity().apply {
            this.user = savedUser
            title = "Test Evidence"
            description = "Description"
            impact = "High impact"
            complexity = "Complex"
            contribution = "Led the effort"
            status = "MANAGER_ASSESSED"
            createdDate = LocalDate.now()
            lastModifiedDate = LocalDate.now()
        }
        val savedEvidence = evidenceJpaRepository.save(evidence)

        val checkIn = CheckInEntity().apply {
            this.user = savedUser
            this.manager = savedManager
            managerNotes = "Good progress this quarter."
            status = "DRAFT"
            checkInDate = LocalDate.now()
            holisticScores["THINKS"] = CheckInEntity.PillarScoreValue(3, savedEvidence.id)
            holisticScores["ENGAGES"] = CheckInEntity.PillarScoreValue(4, null)
        }

        // When
        val savedCheckIn = checkInRepository.save(checkIn)
        val foundCheckIns = checkInRepository.findByUserId(savedUser.id)

        // Then
        savedCheckIn.id shouldNotBe null
        foundCheckIns shouldHaveSize 1
        val retrieved = foundCheckIns[0]
        retrieved.managerNotes shouldBe "Good progress this quarter."
        retrieved.holisticScores shouldHaveMapSize 2
        retrieved.holisticScores["THINKS"]?.score shouldBe 3
        retrieved.holisticScores["THINKS"]?.evidenceId shouldBe savedEvidence.id
        retrieved.holisticScores["ENGAGES"]?.score shouldBe 4
        retrieved.holisticScores["ENGAGES"]?.evidenceId shouldBe null
    }
}
