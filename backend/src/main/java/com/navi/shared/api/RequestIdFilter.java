package com.navi.shared.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Attaches a request id to the logging context and to the response.
 *
 * <p>Without this, investigating a report like "the dashboard showed the wrong number yesterday"
 * means searching interleaved log lines from concurrent requests. With it, one id gathers every line
 * belonging to a single request.
 *
 * <p>An inbound {@code X-Request-Id} is honoured so that a client or proxy can correlate its own
 * logs with the server's.
 */
@Component
@Order(1)
class RequestIdFilter extends OncePerRequestFilter {

    static final String HEADER = "X-Request-Id";
    static final String MDC_KEY = "requestId";

    /** Bounded so an oversized client header cannot bloat every log line. */
    private static final int MAX_LENGTH = 64;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        MDC.put(MDC_KEY, requestId);
        response.setHeader(HEADER, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            // Threads are reused; leaving the value behind would mislabel the next request.
            MDC.remove(MDC_KEY);
        }
    }

    private static String resolveRequestId(HttpServletRequest request) {
        String provided = request.getHeader(HEADER);
        if (provided == null || provided.isBlank()) {
            return UUID.randomUUID().toString();
        }
        String trimmed = provided.strip();
        return trimmed.length() > MAX_LENGTH ? trimmed.substring(0, MAX_LENGTH) : trimmed;
    }
}
