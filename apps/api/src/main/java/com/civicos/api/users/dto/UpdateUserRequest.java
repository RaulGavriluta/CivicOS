package com.civicos.api.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(
        @NotBlank(message = "First name must not be blank")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        String lastName,

        @Pattern(regexp = "^$|^\\+?[1-9]\\d{1,14}$", message = "Phone number must follow international format (E.164)")
        String phoneNumber
) {
}
