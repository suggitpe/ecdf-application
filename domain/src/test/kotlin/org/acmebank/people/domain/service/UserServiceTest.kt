package org.acmebank.people.domain.service

import org.acmebank.people.domain.Grade
import org.acmebank.people.domain.User
import org.acmebank.people.domain.port.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `should create user`() {
        // Given
        val userId = UUID.randomUUID()
        val grade = Grade(UUID.randomUUID(), "Senior", "Software Engineer", mapOf())
        val userToCreate = User(null, "dev@acmebank.com", "Jane Dev", grade, null, false, false)
        val createdUser = User(userId, "dev@acmebank.com", "Jane Dev", grade, null, false, false)

        `when`(userRepository.save(userToCreate)).thenReturn(createdUser)

        // When
        val result = userService.createUser("dev@acmebank.com", "Jane Dev", grade, null, false, false)

        // Then
        assertEquals(userId, result.id())
        assertEquals("Jane Dev", result.fullName())
        verify(userRepository).save(userToCreate)
    }

    @Test
    fun `should find user by id`() {
        // Given
        val userId = UUID.randomUUID()
        val grade = Grade(UUID.randomUUID(), "Senior", "Software Engineer", mapOf())
        val expectedUser = User(userId, "dev@acmebank.com", "Jane Dev", grade, null, false, false)

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser))

        // When
        val result = userService.getUserById(userId)

        // Then
        assertTrue(result.isPresent)
        assertEquals(expectedUser, result.get())
        verify(userRepository).findById(userId)
    }

    @Test
    fun `should find users by manager id`() {
        // Given
        val managerId = UUID.randomUUID()
        val grade = Grade(UUID.randomUUID(), "Senior", "Software Engineer", mapOf())
        val expectedUser = User(UUID.randomUUID(), "dev@acmebank.com", "Jane Dev", grade, managerId, false, false)

        `when`(userRepository.findByManagerId(managerId)).thenReturn(listOf(expectedUser))

        // When
        val result = userService.getUsersByManagerId(managerId)

        // Then
        assertEquals(1, result.size)
        assertEquals(expectedUser, result[0])
        verify(userRepository).findByManagerId(managerId)
    }
}
