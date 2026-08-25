package com.civicos.api.users;

import com.civicos.api.support.PostgresTestConfiguration;
import com.civicos.api.users.dto.ChangePasswordRequest;
import com.civicos.api.users.dto.CreateUserRequest;
import com.civicos.api.users.dto.UpdateUserRequest;
import com.civicos.api.users.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(PostgresTestConfiguration.class)
@ActiveProfiles("test")
@DisplayName("User API Full Integration Tests")
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete User Lifecycle: Create -> Read -> Update -> Change Password -> Deactivate")
    void shouldExecuteFullUserLifecycle() throws Exception {
        // 1. CREATE USER (POST /users)
        CreateUserRequest createRequest = new CreateUserRequest(
                "alex.andreescu@civicos.ro",
                "SecureP@ssword2026",
                "Alex",
                "Andreescu",
                "+40733112233"
        );

        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("alex.andreescu@civicos.ro"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserResponse.class
        );
        UUID userId = createdUser.id();

        // 2. GET USER BY ID (GET /users/{id})
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("alex.andreescu@civicos.ro"))
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Andreescu"))
                .andExpect(jsonPath("$.phoneNumber").value("+40733112233"));

        // 3. UPDATE USER PROFILE (PUT /users/{id})
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "Alexandru",
                "Andreescu-Popa",
                "+40799887766"
        );

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alexandru"))
                .andExpect(jsonPath("$.lastName").value("Andreescu-Popa"))
                .andExpect(jsonPath("$.phoneNumber").value("+40799887766"));

        // 4. CHANGE PASSWORD (PATCH /users/{id}/password)
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(
                "SecureP@ssword2026",
                "BrandNewSecretPassword123!"
        );

        mockMvc.perform(patch("/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isNoContent());

        // 5. DEACTIVATE USER (DELETE /users/{id})
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        // 6. VERIFY FINAL STATUS IN DATABASE
        User finalUserEntity = userRepository.findById(userId).orElseThrow();
        assertThat(finalUserEntity.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(finalUserEntity.getFirstName()).isEqualTo("Alexandru");
    }

    @Test
    @DisplayName("Should return 409 Conflict ProblemDetail when registering with duplicate email")
    void shouldReturnConflictForDuplicateEmail() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "duplicate@civicos.ro",
                "Password123!",
                "Radu",
                "Enache",
                null
        );

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("User Already Exists"))
                .andExpect(jsonPath("$.status").value(409));
    }
}