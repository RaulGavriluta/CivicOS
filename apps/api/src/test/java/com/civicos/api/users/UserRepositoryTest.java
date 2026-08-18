package com.civicos.api.users;

import com.civicos.api.support.PostgresTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestConfiguration.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {
        User user = UserTestData.activeUser();

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isEqualTo(user.getId());

        User foundUser = userRepository
                .findById(user.getId())
                .orElseThrow();

        assertThat(foundUser.getEmail())
                .isEqualTo("ion.popescu@example.com");

        assertThat(foundUser.getStatus())
                .isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldFindUserByEmail(){
        User user = UserTestData.activeUser();

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail(
                "ion.popescu@example.com"
        );

        assertThat(foundUser)
                .isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(user.getId());
    }
}