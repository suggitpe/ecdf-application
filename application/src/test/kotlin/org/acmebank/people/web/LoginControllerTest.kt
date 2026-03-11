package org.acmebank.people.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.springframework.context.annotation.Import
import org.acmebank.people.application.config.SecurityConfig

@WebMvcTest(LoginController::class)
@Import(SecurityConfig::class)
class LoginControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return login view`() {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk)
            .andExpect(view().name("login"))
    }
}
