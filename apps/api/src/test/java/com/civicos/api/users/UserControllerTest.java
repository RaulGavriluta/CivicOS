package com.civicos.api.users;

import com.civicos.api.users.dto.ChangePasswordRequest;
import com.civicos.api.users.dto.CreateUserRequest;
import com.civicos.api.users.dto.UpdateUserRequest;
import com.civicos.api.users.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Nested
    @DisplayName("GET /users")
    class GetAllUsersEndpoint {

        @Test
        @DisplayName("Should return 200 OK with list of all users")
        void shouldReturnListOfUsers() throws Exception {
            User user = UserTestData.activeUser();
            UserResponse response = UserResponse.fromEntity(user);

            given(userService.getUsers()).willReturn(List.of(response));

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(user.getId().toString()))
                    .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                    .andExpect(jsonPath("$[0].firstName").value(user.getFirstName()))
                    .andExpect(jsonPath("$[0].lastName").value(user.getLastName()))
                    .andExpect(jsonPath("$[0].phoneNumber").value(user.getPhoneNumber()))
                    .andExpect(jsonPath("$[0].status").value(user.getStatus().name()))
                    .andExpect(jsonPath("$[0].passwordHash").doesNotExist());
        }
    }

    @Nested
    @DisplayName("POST /users")
    class CreateUserEndpoint {

        @Test
        @DisplayName("Should return 201 Created and Location header when payload is valid")
        void shouldCreateUserSuccessfully() throws Exception {
            User user = UserTestData.activeUser();
            CreateUserRequest request = new CreateUserRequest(
                    user.getEmail(),
                    "Password123!",
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber()
            );

            given(userService.createUser(any(CreateUserRequest.class))).willReturn(user);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/users/" + user.getId()))
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                    .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                    .andExpect(jsonPath("$.status").value(user.getStatus().name()))
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request validation fails")
        void shouldReturn400WhenValidationFails() throws Exception {
            CreateUserRequest invalidRequest = new CreateUserRequest(
                    "invalid-email",
                    "short",
                    "",
                    "",
                    null
            );

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserByIdEndpoint {

        @Test
        @DisplayName("Should return 200 OK and user details when user exists")
        void shouldReturnUserById() throws Exception {
            User user = UserTestData.activeUser();

            given(userService.getUserById(user.getId())).willReturn(user);

            mockMvc.perform(get("/users/{id}", user.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                    .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                    .andExpect(jsonPath("$.status").value(user.getStatus().name()))
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
    }

    @Nested
    @DisplayName("GET /users?email=...")
    class GetUserByEmailEndpoint {

        @Test
        @DisplayName("Should return 200 OK and user details when searching by email")
        void shouldReturnUserByEmail() throws Exception {
            User user = UserTestData.activeUser();

            given(userService.getUserByEmail(user.getEmail())).willReturn(user);

            mockMvc.perform(get("/users").param("email", user.getEmail()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                    .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                    .andExpect(jsonPath("$.status").value(user.getStatus().name()))
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
    }

    @Nested
    @DisplayName("PUT /users/{id}")
    class UpdateUserEndpoint {

        @Test
        @DisplayName("Should return 200 OK with updated details")
        void shouldUpdateUserSuccessfully() throws Exception {
            User user = UserTestData.activeUser();
            UpdateUserRequest request = new UpdateUserRequest("Mihai", "Vasile", "+40799999999");

            given(userService.updateUser(eq(user.getId()), any(UpdateUserRequest.class))).willReturn(user);

            mockMvc.perform(put("/users/{id}", user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()));
        }
    }

    @Nested
    @DisplayName("DELETE /users/{id}")
    class DeactivateUserEndpoint {

        @Test
        @DisplayName("Should return 204 No Content when deactivating user")
        void shouldDeactivateUserSuccessfully() throws Exception {
            UUID userId = UUID.randomUUID();

            willDoNothing().given(userService).deactivateUser(userId);

            mockMvc.perform(delete("/users/{id}", userId))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("PATCH /users/{id}/password")
    class ChangePasswordEndpoint {

        @Test
        @DisplayName("Should return 204 No Content when password changed successfully")
        void shouldChangePasswordSuccessfully() throws Exception {
            UUID userId = UUID.randomUUID();
            ChangePasswordRequest request = new ChangePasswordRequest("oldPassword123", "newPassword123");

            willDoNothing().given(userService).changePassword(eq(userId), any(ChangePasswordRequest.class));

            mockMvc.perform(patch("/users/{id}/password", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }
    }
}