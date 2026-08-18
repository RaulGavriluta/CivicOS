package com.civicos.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldHashAndVerifyPassword() {
        String rawPassword = "secret-password";

        String hash = passwordEncoder.encode(rawPassword);

        assertThat(hash)
                .isNotEqualTo(rawPassword);

        assertThat(passwordEncoder.matches(rawPassword, hash))
                .isTrue();
    }
}