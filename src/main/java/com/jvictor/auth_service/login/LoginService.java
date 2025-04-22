package com.jvictor.auth_service.login;

import com.jvictor.auth_service.refresh_token.RefreshTokenService;
import com.jvictor.auth_service.security.JwtUtil;
import com.jvictor.auth_service.user.User;
import com.jvictor.auth_service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("Logging in user: " + loginRequest.getUsername());
        authenticateUser(loginRequest);
        User user = userService.loadUserByUsername(loginRequest.getUsername());
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    private void authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword());

        authenticationManager.authenticate(authentication);
    }


}
