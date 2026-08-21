package com.civicos.api.users;

import com.civicos.api.users.dto.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserByIdOperations {

        @Test
        @DisplayName("Should return user when entity exists for given ID")
        void shouldReturnUserWhenUserExists() {
            User user = UserTestData.activeUser();

            when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));

            User result = userService.getUserById(user.getId());

            assertThat(result).isEqualTo(user);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when entity is not found by ID")
        void shouldThrowExceptionWhenUserDoesNotExist() {
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found:" + userId);
        }
    }

    @Nested
    @DisplayName("getUserByEmail()")
    class GetUserByEmailOperations {

        @Test
        @DisplayName("Should return user when entity exists for given email")
        void shouldReturnUserWhenEmailExists() {
            User user = UserTestData.activeUser();

            when(userRepository.findByEmail(user.getEmail()))
                    .thenReturn(Optional.of(user));

            User result = userService.getUserByEmail(user.getEmail());

            assertThat(result).isEqualTo(user);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when entity is not found by email")
        void shouldThrowUserNotFoundExceptionWhenEmailDoesNotExist() {
            String email = "missing@example.com";

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserByEmail(email))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found: " + email);
        }
    }

    @Nested
    @DisplayName("createUser()")
    class CreateUserOperations {

        @Test
        @DisplayName("Should hash password with Argon2id and persist active user")
        void shouldCreateUserSuccessfully() {
            CreateUserRequest request = new CreateUserRequest(
                    "ion.popescu@example.com",
                    "secret-password",
                    "Ion",
                    "Popescu"
            );

            when(userRepository.findByEmail(request.email()))
                    .thenReturn(Optional.empty());

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            User createdUser = userService.createUser(request);

            assertThat(createdUser.getId()).isNotNull();
            assertThat(createdUser.getEmail()).isEqualTo(request.email());
            assertThat(createdUser.getPasswordHash()).isNotEqualTo(request.password());
            assertThat(passwordEncoder.matches(request.password(), createdUser.getPasswordHash())).isTrue();
            assertThat(createdUser.getFirstName()).isEqualTo(request.firstName());
            assertThat(createdUser.getLastName()).isEqualTo(request.lastName());
            assertThat(createdUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(createdUser.getCreatedAt()).isNotNull();
            assertThat(createdUser.getUpdatedAt()).isNotNull();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email is already registered")
        void shouldThrowUserAlreadyExistsExceptionWhenEmailAlreadyExists() {
            User existingUser = UserTestData.activeUser();

            when(userRepository.findByEmail(existingUser.getEmail()))
                    .thenReturn(Optional.of(existingUser));

            CreateUserRequest request = new CreateUserRequest(
                    existingUser.getEmail(),
                    "secret-password",
                    "Ion",
                    "Popescu"
            );

            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists: " + existingUser.getEmail());
        }
    }
}