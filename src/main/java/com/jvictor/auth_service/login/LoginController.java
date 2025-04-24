package com.jvictor.auth_service.login;

import com.jvictor.auth_service.commons.AuthenticationTokens;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public AuthenticationTokens login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }

}
