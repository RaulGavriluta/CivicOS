package com.civicos.api.users.dto;

import com.civicos.api.users.User;
import com.civicos.api.users.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    @DisplayName("Should correctly map User entity to UserResponse DTO")
    void shouldMapFromEntityCorrectly() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setId(id);
        user.setEmail("andrei.ionescu@civicos.ro");
        user.setPasswordHash("$argon2id$v=19$m=65536,t=3,p=4$secretHash");
        user.setFirstName("Andrei");
        user.setLastName("Ionescu");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserResponse response = UserResponse.fromEntity(user);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.email()).isEqualTo("andrei.ionescu@civicos.ro");
        assertThat(response.firstName()).isEqualTo("Andrei");
        assertThat(response.lastName()).isEqualTo("Ionescu");
        assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("UserResponse record should have proper immutability and accessor methods")
    void shouldSupportDirectRecordInstantiation() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserResponse response = new UserResponse(
                id,
                "elena.radu@civicos.ro",
                "Elena",
                "Radu",
                UserStatus.ACTIVE,
                now,
                now
        );

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.email()).isEqualTo("elena.radu@civicos.ro");
        assertThat(response.firstName()).isEqualTo("Elena");
        assertThat(response.lastName()).isEqualTo("Radu");
        assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);
    }
}