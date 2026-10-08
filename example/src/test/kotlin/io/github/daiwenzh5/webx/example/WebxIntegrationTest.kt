package io.github.daiwenzh5.webx.example

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class WebxIntegrationTest {

    @Autowired
    lateinit var wac: WebApplicationContext

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.webAppContextSetup(wac).build()
    }

    @Test
    fun `GET user is wrapped as R`() {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.id").value(1))
    }

    @Test
    fun `GET health is not wrapped because of @RawResponse`() {
        mockMvc.perform(get("/api/users/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
    }

    @Test
    fun `GET unknown user returns R with error code`() {
        mockMvc.perform(get("/api/users/0"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(4040))
            .andExpect(jsonPath("$.message").value("User not found"))
    }

    @Test
    fun `X-HTTP-Method-Override header rewrites POST to DELETE`() {
        mockMvc.perform(
            post("/api/users/42")
                .header("X-HTTP-Method-Override", "DELETE")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.deleted").value(42))
    }

    @Test
    fun `response header carries traceId`() {
        mockMvc.perform(get("/api/users/1").header("X-Trace-Id", "test-trace-001"))
            .andExpect(status().isOk)
            .andExpect(header().string("X-Trace-Id", "test-trace-001"))
    }
}
