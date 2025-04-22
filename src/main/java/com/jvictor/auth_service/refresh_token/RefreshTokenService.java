package com.jvictor.auth_service.refresh_token;

import com.jvictor.auth_service.user.User;
import com.jvictor.auth_service.user.UserService;
import com.jvictor.auth_service.utils.HashingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.jvictor.auth_service.utils.HashingUtils.SECRET_SALT;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final HashingUtils hashingUtils;
    private final UserService userService;

    public String createRefreshToken(String username) {
        String uuid = UUID.randomUUID().toString();
        User user = userService.loadUserByUsername(username);
        RefreshToken refreshToken = buildRefreshToken(user, uuid);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public String createRefreshToken(User user) {
        System.out.println("creating refresh token for user: " + user.getEmail());
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = buildRefreshToken(user, token);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public String getRefreshToken(String token) {
        String hashedToken = hashingUtils.hashToken(token);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        return token;
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteById(token);
    }

    private RefreshToken buildRefreshToken(User user, String uuid) {
        String hashedToken = hashingUtils.hashToken(uuid);

        return RefreshToken.builder()
                .token(hashedToken)
                .userId(user.getId())
                .salt(SECRET_SALT)
                .isRevoked(false)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
    }
}
