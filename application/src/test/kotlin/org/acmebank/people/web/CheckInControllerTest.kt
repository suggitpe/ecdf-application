package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.CheckInRepository
import org.acmebank.people.domain.port.GradeRepository
import org.acmebank.people.domain.port.UserRepository
import org.acmebank.people.domain.service.CheckInService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.ArgumentMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.*

@WebMvcTest(CheckInController::class)
class CheckInControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var checkInService: CheckInService

    @MockitoBean
    private lateinit var checkInRepository: CheckInRepository

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var gradeRepository: GradeRepository

    private val userId = UUID.randomUUID()
    private val managerId = UUID.randomUUID()
    private val checkInId = UUID.randomUUID()

    private val mockUser = User(
        userId, "dev@example.com", "Jane Dev",
        Grade(UUID.randomUUID(), "Senior Engineer", "Engineering", emptyMap()),
        managerId, false, false
    )

    private val mockManager = User(
        managerId, "mgr@example.com", "Manager Bob",
        Grade(UUID.randomUUID(), "Manager", "Engineering", emptyMap()),
        null, true, false
    )

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show check-in history for user`() {
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(checkInRepository.findByUserId(userId)).thenReturn(emptyList())

        mockMvc.perform(get("/checkins/user/$userId"))
            .andExpect(status().isOk)
            .andExpect(view().name("checkin-list"))
            .andExpect(model().attributeExists("developer", "checkins"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show new check-in form`() {
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(gradeRepository.findAll()).thenReturn(emptyList())
        `when`(checkInService.getAggregatedScores(userId)).thenReturn(emptyMap())

        mockMvc.perform(get("/checkins/new/$userId"))
            .andExpect(status().isOk)
            .andExpect(view().name("checkin-form"))
            .andExpect(model().attributeExists("developer", "grades", "pillars", "actualScores"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should submit new check-in and redirect`() {
        val targetGradeId = UUID.randomUUID()
        val targetGrade = Grade(targetGradeId, "Staff Engineer", "Engineering", emptyMap())

        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockManager))
        `when`(gradeRepository.findById(targetGradeId)).thenReturn(Optional.of(targetGrade))
        `when`(checkInService.createCheckIn(eq(userId), eq(managerId), anyString(), eq(targetGrade), anyBoolean()))
            .thenReturn(CheckIn(checkInId, userId, managerId, emptyMap<Pillar, PillarScoreInfo>(), "Good", CheckInStatus.ON_TRACK, LocalDate.now()))

        mockMvc.perform(
            post("/checkins/new/$userId")
                .param("targetGradeId", targetGradeId.toString())
                .param("managerNotes", "Excellent progress this quarter.")
                .param("action", "finalize")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/checkins/user/$userId"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show check-in detail`() {
        val checkIn = CheckIn(
            checkInId,
            userId,
            managerId,
            mapOf(Pillar.DESIGNS to PillarScoreInfo(Score(3), UUID.randomUUID())),
            "Good progress",
            CheckInStatus.ON_TRACK,
            LocalDate.now()
        )
        `when`(checkInRepository.findById(checkInId)).thenReturn(Optional.of(checkIn))
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockManager))

        mockMvc.perform(get("/checkins/$checkInId"))
            .andExpect(status().isOk)
            .andExpect(view().name("checkin-detail"))
            .andExpect(model().attributeExists("checkIn", "developer", "isManager", "pillars"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show check-in edit form for draft`() {
        val checkIn = CheckIn(checkInId, userId, managerId, emptyMap<Pillar, PillarScoreInfo>(), "Good", CheckInStatus.DRAFT, LocalDate.now())
        `when`(checkInRepository.findById(checkInId)).thenReturn(Optional.of(checkIn))
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(gradeRepository.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/checkins/$checkInId/edit"))
            .andExpect(status().isOk)
            .andExpect(view().name("checkin-edit"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should update and finalize check-in`() {
        val targetGradeId = UUID.randomUUID()
        val targetGrade = Grade(targetGradeId, "Staff Engineer", "Engineering", emptyMap())
        val updated = CheckIn(checkInId, userId, managerId, emptyMap<Pillar, PillarScoreInfo>(), "Final", CheckInStatus.ON_TRACK, LocalDate.now())

        `when`(gradeRepository.findById(targetGradeId)).thenReturn(Optional.of(targetGrade))
        `when`(checkInService.updateCheckIn(eq(checkInId), any(), eq(targetGrade), eq(true))).thenReturn(updated)

        mockMvc.perform(
            post("/checkins/$checkInId/update")
                .param("targetGradeId", targetGradeId.toString())
                .param("managerNotes", "Final notes")
                .param("action", "finalize")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/checkins/user/$userId"))
    }
}
