package com.jvictor.auth_service.confirmation_token;

import com.jvictor.auth_service.user.User;
import com.jvictor.auth_service.utils.HashingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final HashingUtils hashingUtils;

    public String generateConfirmationTokenForUser(User user) {
        System.out.println("generating confirmation token for user: " + user.getEmail());
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = buildConfirmationToken(user, token);
        confirmationTokenRepository.save(confirmationToken);
        return token;
    }

    public void saveConfirmedToken(ConfirmationToken confirmationToken) {
        if (confirmationToken.getConfirmedAt() == null) {
            throw new IllegalStateException("token not confirmed");
        }
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        String hashedToken = hashingUtils.hashToken(token);
        return confirmationTokenRepository.findByToken(hashedToken);
    }


    private ConfirmationToken buildConfirmationToken(User user, String uuid) {
        String hashedToken = hashingUtils.hashToken(uuid);

        return ConfirmationToken.builder()
                .token(hashedToken)
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }
}
