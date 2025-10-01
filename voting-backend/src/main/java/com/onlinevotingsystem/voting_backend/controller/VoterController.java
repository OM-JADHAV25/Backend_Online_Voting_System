package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.service.UserService;
import com.onlinevotingsystem.voting_backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/voters")
public class VoterController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public VoterController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Send OTP endpoint matching frontend expectation
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            String voterId = body.get("voterID"); // Note: frontend sends "voterID"

            if (voterId == null || voterId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Voter ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Call the service method that generates OTP
            userService.requestOtp(voterId);

            response.put("success", true);
            response.put("message", "OTP sent successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Verify OTP endpoint matching frontend expectation
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            String voterId = body.get("voterID"); // Note: frontend sends "voterID"
            String otp = body.get("otp");

            if (voterId == null || voterId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Voter ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (otp == null || otp.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "OTP is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify OTP and get JWT token
            String token = userService.verifyOtp(voterId, otp);

            response.put("success", true);
            response.put("message", "OTP verified successfully");
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get voter details by voter ID (for dashboard)
    @GetMapping("/{voterId}")
    public ResponseEntity<?> getVoterDetails(@PathVariable String voterId,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract and validate JWT token
            String token = authHeader.replace("Bearer ", "");
            String tokenVoterId = jwtUtil.extractVoterId(token);

            // Ensure the voter can only access their own data
            if (!tokenVoterId.equals(voterId)) {
                return ResponseEntity.status(403).body("Access denied");
            }

            User voter = userService.getUserDetails(voterId);

            // Create response object with voting history and other details
            Map<String, Object> voterData = new HashMap<>();
            voterData.put("voterId", voter.getVoterId());
            voterData.put("name", voter.getName());
            voterData.put("fullName", voter.getName()); // Frontend expects fullName
            voterData.put("email", voter.getEmail());
            voterData.put("constituency", voter.getConstituency());
            voterData.put("status", "ACTIVE"); // You can add this field to User model
            voterData.put("registrationDate", voter.getRegistrationDate());
            voterData.put("hasVoted", false); // You'll need to implement this logic

            // Add mock voting history for now - replace with actual data later
            voterData.put("votingHistory", new java.util.ArrayList<>());

            return ResponseEntity.ok(voterData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}