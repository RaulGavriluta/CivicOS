package com.civicos.api.users;

import com.civicos.api.users.dto.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    void shouldReturnUserWhenUserExists() {
        User user = UserTestData.activeUser();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        User result = userService.getUser(user.getId());

        assertThat(result)
                .isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found:" + userId);
    }

    @Test
    void shouldReturnUserWhenEmailExists() {
        User user = UserTestData.activeUser();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(user.getEmail());

        assertThat(result)
                .isEqualTo(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenEmailDoesNotExist() {
        String email = "missing@example.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found: " + email);
    }

    @Test
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

    @Test
    void shouldCreateUser() {
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

        assertThat(createdUser.getId())
                .isNotNull();

        assertThat(createdUser.getEmail())
                .isEqualTo(request.email());

        assertThat(createdUser.getPasswordHash())
                .isNotEqualTo(request.password());

        assertThat(passwordEncoder.matches(
                request.password(),
                createdUser.getPasswordHash()
        )).isTrue();

        assertThat(createdUser.getFirstName())
                .isEqualTo(request.firstName());

        assertThat(createdUser.getLastName())
                .isEqualTo(request.lastName());

        assertThat(createdUser.getStatus())
                .isEqualTo(UserStatus.ACTIVE);

        assertThat(createdUser.getCreatedAt())
                .isNotNull();

        assertThat(createdUser.getUpdatedAt())
                .isNotNull();

        verify(userRepository).save(any(User.class));
    }
}