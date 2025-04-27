package com.jvictor.auth_service.blocked_ip;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.jvictor.auth_service.utils.HttpUtils.extractIpAddress;

@Service
@RequiredArgsConstructor
public class BlockedIpFilter extends OncePerRequestFilter {

    private final BlockedIpService blockedIpService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = extractIpAddress(request);

        if (blockedIpService.isIpBlocked(ipAddress)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Access forbidden: your IP is temporarily blocked.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
