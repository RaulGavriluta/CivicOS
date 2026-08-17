package com.civicos.api.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserWhenUserExists(){
        User user = UserTestData.activeUser();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        User result = userService.getUser(user.getId());

        assertThat(result)
                .isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist(){
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found:" + userId);
    }

    @Test
    void shouldReturnUserWhenEmailExists(){
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
}
