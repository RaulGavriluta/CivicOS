package com.civicos.api.users;

import com.civicos.api.support.PostgresTestConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("Save and Find by ID operations")
    class FindByIdOperations {

        @Test
        @DisplayName("Should persist user entity and retrieve it by primary key ID")
        void shouldSaveAndFindUserById() {
            User user = UserTestData.activeUser();

            User savedUser = userRepository.save(user);

            assertThat(savedUser.getId()).isEqualTo(user.getId());

            User foundUser = userRepository
                    .findById(user.getId())
                    .orElseThrow();

            assertThat(foundUser.getEmail()).isEqualTo("ion.popescu@example.com");
            assertThat(foundUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(foundUser.getFirstName()).isEqualTo("Ion");
            assertThat(foundUser.getLastName()).isEqualTo("Popescu");
        }
    }

    @Nested
    @DisplayName("Find by Email operations")
    class FindByEmailOperations {

        @Test
        @DisplayName("Should find persisted user by unique email address")
        void shouldFindUserByEmail() {
            User user = UserTestData.activeUser();
            userRepository.save(user);

            Optional<User> foundUser = userRepository.findByEmail("ion.popescu@example.com");

            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getId()).isEqualTo(user.getId());
            assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
        }

        @Test
        @DisplayName("Should return empty optional when querying non-existent email")
        void shouldReturnEmptyWhenEmailNotFound() {
            Optional<User> foundUser = userRepository.findByEmail("non.existent@example.com");

            assertThat(foundUser).isEmpty();
        }
    }
}