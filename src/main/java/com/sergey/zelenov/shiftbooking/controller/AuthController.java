package com.sergey.zelenov.shiftbooking.controller;

import com.sergey.zelenov.shiftbooking.dto.UserRequest;
import com.sergey.zelenov.shiftbooking.exception.UsernameAlreadyTakenException;
import com.sergey.zelenov.shiftbooking.model.Role;
import com.sergey.zelenov.shiftbooking.model.User;
import com.sergey.zelenov.shiftbooking.security.JWTUtil;
import com.sergey.zelenov.shiftbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody UserRequest user) {
        try {
            User registeredUser = userService.registerUser(user.getUsername(), user.getPassword(), Role.ROLE_USER);
            String token = jwtUtil.generateToken(registeredUser);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.status(201).body(response);
        } catch (UsernameAlreadyTakenException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(409).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserRequest user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            User authenticatedUser = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(authenticatedUser);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
}
