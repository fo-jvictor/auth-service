package com.jvictor.auth_service.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
