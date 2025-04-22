package com.jvictor.auth_service.registration;

import com.jvictor.auth_service.confirmation_token.ConfirmationToken;
import com.jvictor.auth_service.confirmation_token.ConfirmationTokenService;
import com.jvictor.auth_service.email.EmailSender;
import com.jvictor.auth_service.user.User;
import com.jvictor.auth_service.user.UserRole;
import com.jvictor.auth_service.user.UserService;
import com.jvictor.auth_service.utils.HashingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final HashingUtils hashingUtils;

    public String register(RegistrationRequest registrationRequest) {
        System.out.println("registering user: " + registrationRequest.getEmail());
        if (registrationRequest.getEmail() == null || registrationRequest.getEmail().isBlank()) {
            throw new RuntimeException("email cannot be null");
        }

        User user = buildRegistrationUser(registrationRequest);

        String token = userService.signUpUser(user);

        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;

        emailSender.sendEmail(registrationRequest.getEmail(),
                RegistrationConstants.EMAIL_CONFIRMATION_SUBJECT,
                RegistrationConstants.EMAIL_CONFIRMATION_CONTENT + link);

        return token;
    }

    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        User user = userService.getUserById(confirmationToken.getUserId());
        user.setEnabled(RegistrationConstants.EMAIL_CONFIRMED_SUCCESSFULLY);

        userService.saveUser(user);
        confirmationTokenService.saveConfirmedToken(confirmationToken);

        return RegistrationConstants.EMAIL_CONFIRMATION_SUCCESS;
    }

    private User buildRegistrationUser(RegistrationRequest registrationRequest) {
        return new User(registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword(),
                Boolean.FALSE,
                RegistrationConstants.PENDING_EMAIL_CONFIRMATION,
                UserRole.USER.name());
    }

}
