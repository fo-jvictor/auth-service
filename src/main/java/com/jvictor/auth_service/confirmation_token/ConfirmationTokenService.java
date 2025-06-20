package com.jvictor.auth_service.confirmation_token;

import com.jvictor.auth_service.email.EmailSender;
import com.jvictor.auth_service.registration.RegistrationConstants;
import com.jvictor.auth_service.user.User;
import com.jvictor.auth_service.user.UserService;
import com.jvictor.auth_service.utils.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final HashService hashService;
    private final UserService userService;
    private final EmailSender emailSender;

    public String generateConfirmationTokenForUser(User user) {
        System.out.println("generating confirmation token for user: " + user.getEmail());
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = buildConfirmationToken(user, token);
        confirmationTokenRepository.save(confirmationToken);
        return token;
    }

    public String resendConfirmationToken(ResendConfirmationTokenRequest request) {
        User user = userService.loadUserByUsername(request.getEmail());
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findByUserId(user.getId());

        //expiring every confirmation token user might have before sending a new one
        confirmationTokens.forEach(confirmationToken -> {
            confirmationToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        });

        String newConfirmationToken = generateConfirmationTokenForUser(user);
        String link = "http://localhost:8080/confirm?token=" + newConfirmationToken;


        emailSender.sendEmail(request.getEmail(), RegistrationConstants.EMAIL_CONFIRMATION_SUBJECT,
                RegistrationConstants.EMAIL_CONFIRMATION_CONTENT + link);

        return "If your email exists a new confirmation was sent.";
    }

    public void saveConfirmedToken(ConfirmationToken confirmationToken) {
        if (confirmationToken.getConfirmedAt() == null) {
            throw new IllegalStateException("token not confirmed");
        }
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        String hashedToken = hashService.hashRawValue(token);
        return confirmationTokenRepository.findByToken(hashedToken);
    }

    private ConfirmationToken buildConfirmationToken(User user, String uuid) {
        String hashedToken = hashService.hashRawValue(uuid);

        return ConfirmationToken.builder()
                .token(hashedToken)
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }
}
