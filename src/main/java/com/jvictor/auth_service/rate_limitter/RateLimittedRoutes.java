package com.jvictor.auth_service.rate_limitter;

import java.util.List;

public class RateLimittedRoutes {
    public static final String LOGIN = "/login";
    public static final String REGISTRATION = "/registration";

    public static final List<String> RATE_LIMITED_ROUTES = List.of(
            LOGIN,
            REGISTRATION
    );
}
