package com.sergey.zelenov.shiftbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergey.zelenov.shiftbooking.dto.UserRequest;
import com.sergey.zelenov.shiftbooking.model.Role;
import com.sergey.zelenov.shiftbooking.model.User;
import com.sergey.zelenov.shiftbooking.repository.UserRepository;
import com.sergey.zelenov.shiftbooking.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JWTUtil jwtUtil;

    private final String username = "existingUser";
    private final String password= "passw0rd";
    private User existingUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword(passwordEncoder.encode(password));
        existingUser.setRole(Role.ROLE_USER);
        userRepository.save(existingUser);

        when(jwtUtil.generateToken(Mockito.any(User.class))).thenReturn("mocked-jwt-token");
    }

    @Test
    public void registerUser_Success() throws Exception {
        String newUserUsername = "newUser";
        String newUserPassword = "newPassword";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(newUserUsername, newUserPassword))))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"token\": \"mocked-jwt-token\"}"));

        Optional<User> userOptional = userRepository.findByUsername(newUserUsername);
        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertTrue(passwordEncoder.matches(newUserPassword, user.getPassword()));
        assertEquals(Role.ROLE_USER, user.getRole());
    }

    @Test
    public void registerUser_Failure_UsernameAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(username, password))))
                .andExpect(status().isConflict())
                .andExpect(content().json("{\"error\": \"Username is already taken\"}"));

        // check that user had not been updated
        Optional<User> user = userRepository.findByUsername(username);
        assertTrue(user.isPresent());
        assertEquals(existingUser.getUpdatedAt(), user.get().getUpdatedAt());
    }

    @Test
    public void registerUser_Failure_EmptyUsername() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("", "validPassword123"))))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertTrue(responseString.contains("Username cannot be empty") ||
                            responseString.contains("Username must be between 3 and 20 characters"));
                });
    }

    @Test
    public void registerUser_Failure_EmptyPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("validUsername", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertTrue(responseString.contains("Password cannot be empty") ||
                            responseString.contains("Password must be at least 8 characters long"));
                });
    }


    @Test
    public void login_Success() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(username, password))))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\": \"mocked-jwt-token\"}"));
    }

    @Test
    public void login_Failure_BadCredentials() throws Exception {
        UserRequest badCredentialsRequest = new UserRequest(username, "wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badCredentialsRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\": \"Invalid credentials\"}"));
    }

    @Test
    public void login_Failure_UserNotFound() throws Exception {
        UserRequest nonExistentUserRequest = new UserRequest("nonExistentUser", password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUserRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\": \"Invalid credentials\"}"));
    }
}
