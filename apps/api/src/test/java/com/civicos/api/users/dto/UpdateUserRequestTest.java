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

@DisplayName("UpdateUserRequest Validation Tests")
class UpdateUserRequestTest {

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
    @DisplayName("Should accept valid update request")
    void shouldAcceptValidRequest() {
        UpdateUserRequest request = new UpdateUserRequest("Mihai", "Popescu", "+40712345678");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank firstName")
    void shouldRejectBlankFirstName() {
        UpdateUserRequest request = new UpdateUserRequest("", "Popescu", "+40712345678");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("firstName");
    }

    @Test
    @DisplayName("Should reject invalid phone number")
    void shouldRejectInvalidPhoneNumber() {
        UpdateUserRequest request = new UpdateUserRequest("Mihai", "Popescu", "invalid-phone");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("phoneNumber");
    }
}