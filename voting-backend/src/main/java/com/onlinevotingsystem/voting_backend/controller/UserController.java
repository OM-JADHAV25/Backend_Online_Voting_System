package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.service.UserService;
import com.onlinevotingsystem.voting_backend.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil; // Inject JwtUtil

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil; // Constructor injection
    }

    // Existing endpoints...

    // ------------------ OTP Endpoints ------------------

    @PostMapping("/request-otp")
    public Map<String, Object> requestOtp(@RequestBody Map<String, String> body) throws Exception {
        String voterId = body.get("voterId");

        String otp = userService.requestOtp(voterId);

        return Map.of(
                "message", "OTP generated (Demo Mode)",
                "otp", otp,
                "demo", true
        );
    }


        @PostMapping("/verify-otp")
    public Map<String, String> verifyOtp(@RequestBody Map<String, String> body) throws Exception {
        String voterId = body.get("voterId");
        String otp = body.get("otp");
        String token = userService.verifyOtp(voterId, otp);
        return Map.of("token", token);
    }

    @GetMapping("/dashboard")
    public User getDashboard(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        String voterId = jwtUtil.extractVoterId(token); // Use instance method
        return userService.getUserDetails(voterId);
    }
}