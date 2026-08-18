package com.civicos.api.users;

import java.time.LocalDateTime;
import java.util.UUID;

public final class UserTestData {

    private UserTestData() {
    }

    public static User activeUser() {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail("ion.popescu@example.com");
        user.setPasswordHash("hashed-password");
        user.setFirstName("Ion");
        user.setLastName("Popescu");
        user.setStatus(UserStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }
}