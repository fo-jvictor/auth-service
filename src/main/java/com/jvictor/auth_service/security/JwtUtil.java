package com.jvictor.auth_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jvictor.auth_service.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
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
                //TODO: do not user username as subject as JWT can be easily decoded
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(expirationMillis)))
                .sign(Algorithm.HMAC256(secret));
    }

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    public boolean validate(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date expirationDate = JWT.decode(token).getExpiresAt();
        return expirationDate.before(new Date());
    }

}
