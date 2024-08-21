package com.sergey.zelenov.shiftbooking;

import com.sergey.zelenov.shiftbooking.controller.AuthController;
import com.sergey.zelenov.shiftbooking.model.Role;
import com.sergey.zelenov.shiftbooking.model.User;
import com.sergey.zelenov.shiftbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);

        when(userService.registerUser(any(String.class), any(String.class), any(Role.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"john_doe\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldRegisterAdminSuccessfully() throws Exception {
        User admin = new User();
        admin.setUsername("admin_user");
        admin.setPassword("admin_password");
        admin.setRole(Role.ROLE_ADMIN);

        when(userService.registerUser(any(String.class), any(String.class), any(Role.class))).thenReturn(admin);

        mockMvc.perform(post("/api/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"admin_user\", \"password\": \"admin_password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin_user"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldReturnForbiddenForNonAdminUser() throws Exception {
        mockMvc.perform(post("/api/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"admin_user\", \"password\": \"admin_password\"}"))
                .andExpect(status().isForbidden());
    }
}
