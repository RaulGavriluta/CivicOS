package com.civicos.api.users.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateUserRequest Validation Tests")
class CreateUserRequestTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("Should accept valid request with all valid fields")
    void shouldAcceptValidRequest() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "Ion",
                "Popescu",
                "+40712345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should accept valid request when optional phoneNumber is null")
    void shouldAcceptRequestWhenPhoneNumberIsNull() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "Ion",
                "Popescu",
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank email")
    void shouldRejectBlankEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "",
                "secret-password",
                "Ion",
                "Popescu",
                "+40712345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("email");
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "not-an-email",
                "secret-password",
                "Ion",
                "Popescu",
                "+40712345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("email");
    }

    @Test
    @DisplayName("Should reject short password")
    void shouldRejectShortPassword() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "short",
                "Ion",
                "Popescu",
                "+40712345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("password");
    }

    @Test
    @DisplayName("Should reject blank firstName")
    void shouldRejectBlankFirstName() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "",
                "Popescu",
                "+40712345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("firstName");
    }

    @Test
    @DisplayName("Should reject blank lastName")
    void shouldRejectBlankLastName() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "Ion",
                "",
                "+40712345678"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("lastName");
    }

    @Test
    @DisplayName("Should reject invalid phoneNumber format")
    void shouldRejectInvalidPhoneNumber() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "Ion",
                "Popescu",
                "invalid-phone-123"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("phoneNumber");
    }
}