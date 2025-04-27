package com.jvictor.auth_service.confirmation_token;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConfirmationTokenController {

    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping("/confirmation-token/resend")
    public String resendConfirmationToken(@RequestBody ResendConfirmationTokenRequest request) {
        return confirmationTokenService.resendConfirmationToken(request);
    }
}
