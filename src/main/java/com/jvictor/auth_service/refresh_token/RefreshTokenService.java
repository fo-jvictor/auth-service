package com.jvictor.auth_service.refresh_token;

import com.jvictor.auth_service.blocked_ip.BlockedIpService;
import com.jvictor.auth_service.commons.AuthenticationTokens;
import com.jvictor.auth_service.security.JwtUtil;
import com.jvictor.auth_service.user.User;
import com.jvictor.auth_service.user.UserService;
import com.jvictor.auth_service.utils.HashingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.jvictor.auth_service.utils.HttpUtils.extractIpAddress;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final HashingUtils hashingUtils;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BlockedIpService blockedIpService;

    public String createRefreshToken(User user, String ipAddress) {
        System.out.println("creating refresh token for user: " + user.getEmail());
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = buildRefreshToken(user, token, ipAddress);
        refreshTokenRepository.save(refreshToken);
        return token;
    }


    /*
    This logic will not allow malicious user to generate access tokens freely
    in case the refresh token is leaked, that's why we are revoking the old refresh token
    and creating a new one, following auth0's best practices.
    https://auth0.com/blog/refresh-tokens-what-are-they-and-when-to-use-them/
    */

    public AuthenticationTokens generateNewAccessTokenForUser(String rawToken) {
        RefreshToken refreshToken = getRefreshToken(rawToken);
        String currentIpAddress = extractIpAddress();

        if (!currentIpAddress.equals(refreshToken.getIpAddress())) {
            refreshToken.setIsRevoked(true);
            refreshTokenRepository.save(refreshToken);
            //saving suspect ip address
            blockedIpService.saveBlockedIp(currentIpAddress);
            throw new IllegalStateException("Invalid refresh token usage from different IP");
        }

        if (refreshToken.getIsRevoked()) {
            throw new IllegalArgumentException("Refresh token is no longer valid.");
        }

        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = userService.getUserById(refreshToken.getUserId());
        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = createRefreshToken(user, currentIpAddress);
        return new AuthenticationTokens(newAccessToken, newRefreshToken);
    }

    private RefreshToken getRefreshToken(String token) {
        String hashedToken = hashingUtils.hashToken(token);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        return refreshToken;
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteById(token);
    }

    private RefreshToken buildRefreshToken(User user, String uuid, String ipAddress) {
        String hashedToken = hashingUtils.hashToken(uuid);

        return RefreshToken.builder()
                .token(hashedToken)
                .userId(user.getId())
                .ipAddress(ipAddress)
                .isRevoked(false)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
    }
}
