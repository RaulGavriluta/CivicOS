package com.civicos.api.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UUID userId,
        String email,
        String firstName,
        String lastName
) {
    public AuthResponse(String accessToken, UUID userId, String email, String firstName, String lastName) {
        this(accessToken, "Bearer", userId, email, firstName, lastName);
    }
}