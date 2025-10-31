package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.model.VotingHistory;
import com.onlinevotingsystem.voting_backend.service.UserService;
import com.onlinevotingsystem.voting_backend.security.JwtUtil;
import com.onlinevotingsystem.voting_backend.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/voters")
public class VoterController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final VoteService voteService;  // ADD THIS

    public VoterController(UserService userService, JwtUtil jwtUtil, VoteService voteService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.voteService = voteService;  // ADD THIS
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
            String token = authHeader.replace("Bearer ", "");
            String tokenVoterId = jwtUtil.extractVoterId(token);

            if (!tokenVoterId.equals(voterId)) {
                return ResponseEntity.status(403).body("Access denied");
            }

            User voter = userService.getUserDetails(voterId);

            // Get voting history
            List<VotingHistory> votingHistory = voteService.getVotingHistory(voter);

            // Create response object
            Map<String, Object> voterData = new HashMap<>();
            voterData.put("voterId", voter.getVoterId());
            voterData.put("name", voter.getName());
            voterData.put("fullName", voter.getName());
            voterData.put("email", voter.getEmail());
            voterData.put("dateOfBirth", voter.getDateOfBirth());
            voterData.put("phoneNumber", voter.getPhoneNumber());
            voterData.put("constituency", voter.getConstituency());
            voterData.put("status", voter.getStatus() != null ? voter.getStatus() : "ACTIVE");
            voterData.put("registrationDate", voter.getRegistrationDate());
            voterData.put("votedElections", voteService.getElectionsVotedByUser(voter));

            // Transform voting history for frontend
            List<Map<String, Object>> historyList = votingHistory.stream()
                    .map(h -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("election", h.getElectionName());
                        map.put("type", h.getElectionType() != null ? h.getElectionType() : "General");
                        map.put("candidate", h.getCandidateName());
                        map.put("party", h.getCandidateParty());
                        map.put("date", h.getVotedAt().toString());
                        map.put("reference", h.getReferenceNumber());
                        map.put("status", "VERIFIED");
                        return map;
                    })
                    .toList();

            voterData.put("votingHistory", historyList);

            return ResponseEntity.ok(voterData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}