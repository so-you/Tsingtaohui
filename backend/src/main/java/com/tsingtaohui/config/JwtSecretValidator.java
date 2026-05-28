package com.tsingtaohui.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class JwtSecretValidator {

    private final JwtProperties jwtProperties;

    public JwtSecretValidator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void validateSecret() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters. Set JWT_SECRET env var.");
        }
        if (secret.contains("default-secret-key-must-be-replaced")) {
            throw new IllegalStateException("JWT secret contains default value. Set JWT_SECRET env var for production.");
        }
    }
}
