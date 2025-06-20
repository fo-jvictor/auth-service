package com.jvictor.auth_service.login_audit;

import com.jvictor.auth_service.utils.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.jvictor.auth_service.utils.HttpUtils.getCurrentUserAgent;

@Component
@RequiredArgsConstructor
public class LoginAuditListener {

    private final LoginAuditRepository loginAuditRepository;
    private final HashService hashService;

    @EventListener
    public void handleSuccess(AuthenticationSuccessEvent event) {
        System.out.println("Login audit listener handle success");
        LoginAudit loginAudit = buildLoginAudit(event);
        loginAuditRepository.save(loginAudit);
    }

    @EventListener
    public void handleFailure(AbstractAuthenticationFailureEvent event) {
        System.out.println("Login audit listener handle failure " + event.getException().getMessage());
        LoginAudit loginAudit = buildLoginAudit(event);
        loginAuditRepository.save(loginAudit);
    }

    private LoginAudit buildLoginAudit(AbstractAuthenticationEvent authenticationEvent) {
        Authentication authentication = authenticationEvent.getAuthentication();

        WebAuthenticationDetails details = authentication != null
                && authentication.getDetails() instanceof WebAuthenticationDetails
                ? (WebAuthenticationDetails) authentication.getDetails()
                : null;

        String username = authentication != null ? authentication.getName() : "unknown";
        String ip = details != null ? details.getRemoteAddress() : "unknown";
        String userAgent = getCurrentUserAgent();

        String hashedIpAddress = hashService.hashRawValue(ip);
        String reason = getReason(authenticationEvent);
        Boolean success = authenticationEvent instanceof AuthenticationSuccessEvent;

        return new LoginAudit(username, hashedIpAddress, userAgent, success, reason, LocalDateTime.now());
    }

    private String getReason(AbstractAuthenticationEvent authenticationEvent) {
        if (authenticationEvent instanceof AbstractAuthenticationFailureEvent failureEvent) {
            return failureEvent.getException().getMessage();
        }
        return null;
    }

}
