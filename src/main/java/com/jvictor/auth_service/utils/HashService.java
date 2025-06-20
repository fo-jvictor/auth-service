package com.jvictor.auth_service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public String hashRawValue(String rawValue) {
        return passwordEncoder.encode(rawValue);
    }

    public Boolean matches(String rawValue, String hashedValue) {
        return passwordEncoder.matches(rawValue, hashedValue);
    }
}
