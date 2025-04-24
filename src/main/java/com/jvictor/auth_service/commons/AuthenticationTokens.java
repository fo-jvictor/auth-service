package com.jvictor.auth_service.commons;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationTokens {
    private String accessToken;
    private String refreshToken;
}
