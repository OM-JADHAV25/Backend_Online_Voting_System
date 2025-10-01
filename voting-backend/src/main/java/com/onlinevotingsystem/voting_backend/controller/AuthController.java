package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.service.UserService;
import com.onlinevotingsystem.voting_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    // Register
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        try {
            // Check if user already exists using getUserByEmail method
            if (userService.getUserByEmail(user.getEmail()).isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "User already exists with this email");
                return response;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword())); // encrypt password
            User registeredUser = userService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", registeredUser);
            // Don't include password in response
            registeredUser.setPassword(null);
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Registration failed: " + e.getMessage());
            return response;
        }
    }

    // Login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            // Validate input
            if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Email and password are required");
                return response;
            }

            // Authenticate using AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // If authentication succeeds, generate token
            String token = jwtUtil.generateToken(email);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("email", email);
            return response;

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Login failed: Invalid email or password");
            return response;
        }
    }

    // Test endpoint to verify JWT is working
    @GetMapping("/test")
    public Map<String, String> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a protected endpoint - JWT is working!");
        return response;
    }
}