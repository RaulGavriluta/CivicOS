package com.civicos.api.common.exception;

import com.civicos.api.users.InvalidPasswordException;
import com.civicos.api.users.UserAlreadyExistsException;
import com.civicos.api.users.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should translate UserNotFoundException into 404 ProblemDetail")
    void shouldHandleUserNotFoundException() {
        UUID id = UUID.randomUUID();
        UserNotFoundException ex = new UserNotFoundException(id.toString());

        ProblemDetail problem = exceptionHandler.handleUserNotFound(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("User Not Found");
        assertThat(problem.getDetail()).isEqualTo("User not found: " + id);
        assertThat(problem.getProperties()).isNotNull();
        assertThat(problem.getProperties().get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("Should translate UserAlreadyExistsException into 409 ProblemDetail")
    void shouldHandleUserAlreadyExistsException() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("test@example.com");

        ProblemDetail problem = exceptionHandler.handleUserAlreadyExists(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("User Already Exists");
        assertThat(problem.getDetail()).isEqualTo("User already exists: test@example.com");
        assertThat(problem.getProperties()).isNotNull();
        assertThat(problem.getProperties().get("timestamp")).isNotNull();
    }

    @Test
    @DisplayName("Should translate InvalidPasswordException into 400 ProblemDetail")
    void shouldHandleInvalidPasswordException() {
        InvalidPasswordException ex = new InvalidPasswordException("Current password does not match");

        ProblemDetail problem = exceptionHandler.handleInvalidPassword(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Invalid Credentials");
        assertThat(problem.getDetail()).isEqualTo("Current password does not match");
        assertThat(problem.getProperties()).isNotNull();
        assertThat(problem.getProperties().get("timestamp")).isNotNull();
    }
}