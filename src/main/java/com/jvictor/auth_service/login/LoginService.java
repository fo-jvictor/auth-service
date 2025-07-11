package com.jvictor.auth_service.login;

import com.jvictor.auth_service.commons.AuthenticationTokens;
import com.jvictor.auth_service.refresh_token.RefreshTokenService;
import com.jvictor.auth_service.security.JwtUtil;
import com.jvictor.auth_service.user.User;
import com.jvictor.auth_service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import static com.jvictor.auth_service.utils.HttpUtils.extractIpAddress;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthenticationTokens login(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        System.out.println("Logging in user: " + loginRequest.getUsername());
        authenticateUser(loginRequest, httpServletRequest);
        String ipAddress = extractIpAddress();
        User user = userService.loadUserByUsername(loginRequest.getUsername());
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user, ipAddress);

        return new AuthenticationTokens(accessToken, refreshToken);
    }

    private void authenticateUser(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword());

        WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(httpServletRequest);
        authentication.setDetails(details);

        authenticationManager.authenticate(authentication);
    }

}
