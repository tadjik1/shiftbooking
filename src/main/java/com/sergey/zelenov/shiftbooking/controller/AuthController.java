package com.sergey.zelenov.shiftbooking.controller;

import com.sergey.zelenov.shiftbooking.dto.UserRequest;
import com.sergey.zelenov.shiftbooking.model.Role;
import com.sergey.zelenov.shiftbooking.model.User;
import com.sergey.zelenov.shiftbooking.security.JWTUtil;
import com.sergey.zelenov.shiftbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> registerUser(@RequestBody UserRequest user) {
        User registeredUser = userService.registerUser(user.getUsername(), user.getPassword(), Role.ROLE_USER);
        String token = jwtUtil.generateToken(registeredUser);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest user) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        User authenticatedUser = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(authenticatedUser);
        return ResponseEntity.ok(token);
    }
}
