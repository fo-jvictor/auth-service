package com.jvictor.auth_service.confirmation_token;

import lombok.Data;

@Data
public class ResendConfirmationTokenRequest {
    private String email;
}
