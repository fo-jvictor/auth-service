package com.jvictor.auth_service.rate_limitter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(10, Duration.ofMinutes(30))
                .build();

        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    private Bucket resolveBucket(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return cache.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String path = request.getServletPath();

        if (!RateLimittedRoutes.RATE_LIMITED_ROUTES.contains(path)) {
            doFilterSafe(filterChain, request, response);
            return;
        }

        Bucket bucket = resolveBucket(request);

        if (bucket.tryConsume(1)) {
            doFilterSafe(filterChain, request, response);
            return;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        try {
            response.getWriter().write("{\"message\":\"Rate limit exceeded. Try again after 30 minutes.\"}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void doFilterSafe(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) {
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
