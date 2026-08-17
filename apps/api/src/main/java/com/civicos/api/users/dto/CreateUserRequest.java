package com.civicos.api.users.dto;

public record CreateUserRequest(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
