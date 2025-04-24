package com.jvictor.auth_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jvictor.auth_service.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expirationMillis;

    public String generateToken(User userDetails) {
        return JWT.create()
                .withSubject(userDetails.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(expirationMillis)))
                .sign(Algorithm.HMAC256(secret));
    }

    public String extractUserId(String token) {
        return JWT.decode(token).getSubject();
    }

    public boolean validate(String token, User userDetails) {
        String userId = extractUserId(token);
        return userId.equals(userDetails.getId()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date expirationDate = JWT.decode(token).getExpiresAt();
        return expirationDate.before(new Date());
    }

}
