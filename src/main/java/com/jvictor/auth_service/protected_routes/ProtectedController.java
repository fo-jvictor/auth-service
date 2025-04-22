package com.jvictor.auth_service.protected_routes;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ProtectedController {

    @GetMapping("/protected")
    public String protectedRoute(Authentication authentication) {
        String name = authentication.getName();
        return "Welcome " + name + ", this is a protected route!";
    }
}
