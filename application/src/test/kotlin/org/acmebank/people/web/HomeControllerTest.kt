package org.acmebank.people.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl

import org.springframework.security.test.context.support.WithMockUser

@WebMvcTest(HomeController::class)
class HomeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser
    fun `should redirect to dashboard on root request`() {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/dashboard"))
    }
}
