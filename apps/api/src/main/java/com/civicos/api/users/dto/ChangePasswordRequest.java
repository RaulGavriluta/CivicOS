package com.civicos.api.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest (
    @NotBlank(message = "Current password must not be blank")
    String currentPassword,

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, message = "New password must have at least 8 characters")
    String newPassword
){
}
