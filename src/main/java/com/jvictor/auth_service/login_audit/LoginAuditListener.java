package com.jvictor.auth_service.login_audit;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginAuditListener {

    private final LoginAuditRepository loginAuditRepository;

    @EventListener
    public void handleSuccess(AuthenticationSuccessEvent event) {
        System.out.println("Login audit listener handle success: " + event.getAuthentication().getName());

        Authentication authentication = event.getAuthentication();

        WebAuthenticationDetails details = authentication != null
                && authentication.getDetails() instanceof WebAuthenticationDetails
                ? (WebAuthenticationDetails) authentication.getDetails()
                : null;

        String username = authentication != null ? authentication.getName() : "unknown";
        String ip = details != null ? details.getRemoteAddress() : "unknown";
        String userAgent = this.getCurrentUserAgent();

        LoginAudit loginAudit = new LoginAudit(username,
                ip, userAgent, true, null, LocalDateTime.now());

        loginAuditRepository.save(loginAudit);
    }

    @EventListener
    public void handleFailure(AbstractAuthenticationFailureEvent event) {
        System.out.println("Login audit listener handle failure " + event.getException().getMessage());
        var authentication = event.getAuthentication();

        var details = (authentication.getDetails() instanceof WebAuthenticationDetails)
                ? (WebAuthenticationDetails) authentication.getDetails()
                : null;

        String username = authentication.getName();
        String ip = (details != null) ? details.getRemoteAddress() : "unknown";
        String userAgent = getCurrentUserAgent();

        LoginAudit loginAudit = new LoginAudit(username, ip,
                userAgent, false, event.getException().getMessage(), LocalDateTime.now());

        loginAuditRepository.save(loginAudit);
    }

    private String getCurrentUserAgent() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest().getHeader("User-Agent") : "unknown";
    }
}
