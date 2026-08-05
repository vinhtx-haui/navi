package com.navi.shared.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that exceptions become the right HTTP status and never leak internals.
 *
 * <p>The unknown-path case is a regression test: an earlier version of the handler caught
 * {@code Exception} without extending {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler},
 * so a mistyped URL answered 500. That both misleads clients and buries genuine server errors among
 * routine 404s.
 */
@WebMvcTest
@Import(ApiExceptionHandlerTest.ThrowingController.class)
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("an unknown path is 404, not 500")
    void unknown_path_returns_not_found() throws Exception {
        mockMvc.perform(get("/api/v1/no-such-endpoint"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("a missing resource is 404 with a stable code")
    void resource_not_found_returns_404() throws Exception {
        mockMvc.perform(get("/test-errors/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.code").value("resource.not_found"));
    }

    @Test
    @DisplayName("a broken business rule is 422, not 400 — the request was well-formed")
    void business_rule_violation_returns_422() throws Exception {
        mockMvc.perform(get("/test-errors/business-rule"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("course.prerequisite_not_met"))
                .andExpect(jsonPath("$.detail").value(containsString("Data Structures")));
    }

    @Test
    @DisplayName("an invalid value object is 400 and explains the rule")
    void illegal_argument_returns_400() throws Exception {
        mockMvc.perform(get("/test-errors/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("request.invalid"))
                .andExpect(jsonPath("$.detail").value(containsString("cannot be negative")));
    }

    @Test
    @DisplayName("an unexpected error is 500 and leaks nothing about internals")
    void unexpected_error_hides_internal_detail() throws Exception {
        mockMvc.perform(get("/test-errors/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("internal.error"))
                .andExpect(jsonPath("$.detail").value(not(containsString("jdbc"))))
                .andExpect(jsonPath("$.detail").value(not(containsString("password"))));
    }

    @RestController
    @RequestMapping("/test-errors")
    static class ThrowingController {

        @GetMapping("/not-found")
        void notFound() {
            throw new ResourceNotFound("Course", "CS101");
        }

        @GetMapping("/business-rule")
        void businessRule() {
            throw new BusinessRuleViolation(
                    "course.prerequisite_not_met",
                    "Cannot enroll in Database Systems: prerequisite Data Structures is not completed");
        }

        @GetMapping("/illegal-argument")
        void illegalArgument() {
            throw new IllegalArgumentException("Credit cannot be negative: -3");
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("jdbc connection failed for user navi with password navi_local_dev");
        }
    }
}
