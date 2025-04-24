package com.jvictor.auth_service.refresh_token;

import com.jvictor.auth_service.commons.AuthenticationTokens;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @GetMapping("/refresh-token")
    public AuthenticationTokens generateNewAccessTokenForUser(@RequestParam String rawRefreshToken) {
        return refreshTokenService.generateNewAccessTokenForUser(rawRefreshToken);
    }
}
