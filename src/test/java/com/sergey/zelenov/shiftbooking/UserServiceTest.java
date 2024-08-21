package com.sergey.zelenov.shiftbooking;

import com.sergey.zelenov.shiftbooking.model.Role;
import com.sergey.zelenov.shiftbooking.model.User;
import com.sergey.zelenov.shiftbooking.repository.UserRepository;
import com.sergey.zelenov.shiftbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        User registeredUser = userService.registerUser("john_doe", "password123", Role.ROLE_USER);
        assertNotNull(registeredUser.getId());
        assertEquals("john_doe", registeredUser.getUsername());
        assertTrue(passwordEncoder.matches("password123", registeredUser.getPassword()));
        assertEquals(Role.ROLE_USER, registeredUser.getRole());
    }

    @Test
    void shouldFailToRegisterUserWhenUsernameIsTaken() {
        userService.registerUser("john_doe", "password123", Role.ROLE_USER);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("john_doe", "password456", Role.ROLE_USER);
        });
        assertEquals("Username is already taken", thrown.getMessage());
    }

    @Test
    void shouldUpdateUserProfileSuccessfully() {
        User user = userService.registerUser("john_doe", "password123", Role.ROLE_USER);
        User updatedUser = userService.updateUser(user.getId(), "new_john_doe", "new_password123");
        assertEquals("new_john_doe", updatedUser.getUsername());
        assertTrue(passwordEncoder.matches("new_password123", updatedUser.getPassword()));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        User user = userService.registerUser("john_doe", "password123", Role.ROLE_USER);
        userService.deleteUser(user.getId());
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void shouldGetCurrentUserSuccessfully() {
        User user = userService.registerUser("john_doe", "password123", Role.ROLE_USER);
        User currentUser = userService.getUser(user.getId());
        assertEquals("john_doe", currentUser.getUsername());
        assertEquals(user.getId(), currentUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.getUser(999L);
        });
        assertEquals("User not found", thrown.getMessage());
    }
}
