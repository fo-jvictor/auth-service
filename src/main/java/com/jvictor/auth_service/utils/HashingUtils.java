package com.jvictor.auth_service.utils;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class HashingUtils {

    public static final String SECRET_SALT = "replace_with_actual_secure_salt";

    public String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(SECRET_SALT.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = md.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }


}
