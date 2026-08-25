package com.civicos.api.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 100, message = "Password must have at least 8 characters")
        String password,

        @NotBlank(message = "First name must not be blank")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(max = 100)
        String lastName,

        @Pattern(regexp = "^$|^\\+?[1-9]\\d{1,14}$", message = "Phone number must follow international format (E.164)")
        String phoneNumber
) {
}