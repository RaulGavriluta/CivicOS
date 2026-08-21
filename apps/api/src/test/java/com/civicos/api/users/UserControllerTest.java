package com.civicos.api.users;

import com.civicos.api.users.dto.CreateUserRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("POST /api/users")
    class CreateUserEndpoint {

        @Test
        @DisplayName("Should return 201 Created and Location header when payload is valid")
        void shouldCreateUserSuccessfully() throws Exception {
            User user = UserTestData.activeUser();
            CreateUserRequest request = new CreateUserRequest(
                    user.getEmail(),
                    "Password123!",
                    user.getFirstName(),
                    user.getLastName()
            );

            given(userService.createUser(any(CreateUserRequest.class))).willReturn(user);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/api/users/" + user.getId()))
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(user.getLastName()))
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
                    ""
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserByIdEndpoint {

        @Test
        @DisplayName("Should return 200 OK and user details when user exists")
        void shouldReturnUserById() throws Exception {
            User user = UserTestData.activeUser();

            given(userService.getUserById(user.getId())).willReturn(user);

            mockMvc.perform(get("/api/users/{id}", user.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.status").value(user.getStatus().name()))
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
    }

    @Nested
    @DisplayName("GET /api/users?email=...")
    class GetUserByEmailEndpoint {

        @Test
        @DisplayName("Should return 200 OK and user details when searching by email")
        void shouldReturnUserByEmail() throws Exception {
            User user = UserTestData.activeUser();

            given(userService.getUserByEmail(user.getEmail())).willReturn(user);

            mockMvc.perform(get("/api/users").param("email", user.getEmail()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.status").value(user.getStatus().name()))
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
    }
}