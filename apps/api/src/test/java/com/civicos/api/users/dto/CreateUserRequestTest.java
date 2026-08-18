package com.civicos.api.users.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
    void shouldAcceptValidRequest() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "Ion",
                "Popescu"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .isEmpty();
    }

    @Test
    void shouldRejectBlankEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "",
                "secret-password",
                "Ion",
                "Popescu"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("email");
    }

    @Test
    void shouldRejectInvalidEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "not-an-email",
                "secret-password",
                "Ion",
                "Popescu"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("email");
    }

    @Test
    void shouldRejectShortPassword() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "short",
                "Ion",
                "Popescu"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("password");
    }

    @Test
    void shouldRejectBlankFirstName() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "",
                "Popescu"
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("firstName");
    }

    @Test
    void shouldRejectBlankLastName() {
        CreateUserRequest request = new CreateUserRequest(
                "ion.popescu@example.com",
                "secret-password",
                "Ion",
                ""
        );

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("lastName");
    }
}