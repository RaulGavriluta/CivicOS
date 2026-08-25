package com.civicos.api.users;

import com.civicos.api.users.dto.ChangePasswordRequest;
import com.civicos.api.users.dto.CreateUserRequest;
import com.civicos.api.users.dto.UpdateUserRequest;
import com.civicos.api.users.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
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
    @DisplayName("getUsers()")
    class GetUsers {

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            User user = UserTestData.activeUser();

            when(userRepository.findAll())
                    .thenReturn(List.of(user));

            List<UserResponse> result = userService.getUsers();

            assertThat(result)
                    .hasSize(1)
                    .containsExactly(UserResponse.fromEntity(user));
        }
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
                    "Popescu",
                    "+40712345678"
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
            assertThat(createdUser.getPhoneNumber()).isEqualTo(request.phoneNumber());
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
                    "Popescu",
                    null
            );

            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists: " + existingUser.getEmail());
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserOperations {

        @Test
        @DisplayName("Should update user details when user exists")
        void shouldUpdateUserDetailsSuccessfully() {
            User user = UserTestData.activeUser();
            UpdateUserRequest request = new UpdateUserRequest("Mihai", "Vasile", "+40799999999");

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User updatedUser = userService.updateUser(user.getId(), request);

            assertThat(updatedUser.getFirstName()).isEqualTo("Mihai");
            assertThat(updatedUser.getLastName()).isEqualTo("Vasile");
            assertThat(updatedUser.getPhoneNumber()).isEqualTo("+40799999999");
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("deactivateUser()")
    class DeactivateUserOperations {

        @Test
        @DisplayName("Should change user status to INACTIVE")
        void shouldDeactivateUserSuccessfully() {
            User user = UserTestData.activeUser();

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.deactivateUser(user.getId());

            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("changePassword()")
    class ChangePasswordOperations {

        @Test
        @DisplayName("Should change password successfully when current password matches")
        void shouldChangePasswordSuccessfully() {
            User user = UserTestData.activeUser();
            String currentPlainPassword = "oldPassword123";
            user.setPasswordHash(passwordEncoder.encode(currentPlainPassword));

            ChangePasswordRequest request = new ChangePasswordRequest(currentPlainPassword, "newSecretPassword123");

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.changePassword(user.getId(), request);

            assertThat(passwordEncoder.matches("newSecretPassword123", user.getPasswordHash())).isTrue();
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw InvalidPasswordException when current password does not match")
        void shouldThrowExceptionWhenCurrentPasswordIsWrong() {
            User user = UserTestData.activeUser();
            user.setPasswordHash(passwordEncoder.encode("correctPassword123"));

            ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newSecretPassword123");

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.changePassword(user.getId(), request))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessage("Current password does not match");
        }
    }
}