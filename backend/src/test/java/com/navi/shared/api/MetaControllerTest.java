package com.navi.shared.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer test only — no database is started, so this runs in well under a second.
 */
@WebMvcTest(MetaController.class)
class MetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("reports which build of the API is answering")
    void returns_api_metadata() throws Exception {
        mockMvc.perform(get("/api/v1/meta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Navi Platform API"))
                .andExpect(jsonPath("$.apiVersion").value("v1"))
                .andExpect(jsonPath("$.phase").value("1 — Foundation"));
    }

    @Test
    @DisplayName("every response carries a request id for log correlation")
    void echoes_a_request_id() throws Exception {
        mockMvc.perform(get("/api/v1/meta").header("X-Request-Id", "test-correlation-id"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", "test-correlation-id"));
    }
}
