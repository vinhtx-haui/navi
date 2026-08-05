package com.navi.shared.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translates exceptions into RFC 9457 {@code application/problem+json} responses.
 *
 * <p>This is the single place where domain concepts become HTTP concepts. Keeping it in one class is
 * what lets the domain layer stay free of {@code ResponseEntity} and status codes.
 *
 * <p>Extends {@link ResponseEntityExceptionHandler} so that Spring MVC's own exceptions keep their
 * correct status codes — an unknown URL is a 404, an unsupported method is a 405, malformed JSON is a
 * 400. Without the base class, the catch-all {@code Exception} handler below swallows all of them and
 * reports 500, which both misleads clients and buries real server errors in noise.
 */
@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFound.class)
    ProblemDetail handleNotFound(ResourceNotFound ex) {
        return problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), ex.code());
    }

    @ExceptionHandler(BusinessRuleViolation.class)
    ProblemDetail handleBusinessRule(BusinessRuleViolation ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Business rule violated", ex.getMessage(), ex.code());
    }

    /**
     * Value-object constructors reject invalid input by throwing this, so it means a bad request.
     *
     * <p>The message is safe to return: these come from {@code Credit}, {@code Grade} and friends,
     * which describe the rule that was broken rather than internal state.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage(), "request.invalid");
    }

    /** Bean Validation failures, reported per field so the client can mark the right inputs. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                 HttpHeaders headers,
                                                                 HttpStatusCode status,
                                                                 WebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = problem(
                HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields are invalid", "request.validation");
        problem.setProperty("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(problem);
    }

    /**
     * Last resort, for exceptions that are genuinely unexpected.
     *
     * <p>The client message is deliberately generic: details go to the log, not over the wire, so
     * that stack traces, SQL and internal identifiers are never exposed.
     */
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error",
                "An unexpected error occurred. Please try again.",
                "internal.error");
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail, String code) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        // Clients branch on this stable code rather than on the human-readable message.
        problem.setProperty("code", code);
        return problem;
    }
}
