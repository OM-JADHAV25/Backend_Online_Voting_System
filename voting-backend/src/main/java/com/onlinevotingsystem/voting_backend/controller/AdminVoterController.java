package com.onlinevotingsystem.voting_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/voters")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminVoterController {
    @GetMapping
    public ResponseEntity<?> getAllVoters(@RequestParam(required = false) String status) {
        // TODO: Implement with actual VoterService
        List<Map<String, Object>> mockVoters = List.of(
                Map.of("id", 1, "name", "John Doe", "email", "john@example.com",
                        "voterId", "V001", "status", "Verified", "registrationDate", "2024-01-15"),
                Map.of("id", 2, "name", "Jane Smith", "email", "jane@example.com",
                        "voterId", "V002", "status", "Pending", "registrationDate", "2024-02-20")
        );
        return ResponseEntity.ok(mockVoters);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveVoter(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Voter approved"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoter(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Voter deleted"));
    }

    @PostMapping
    public ResponseEntity<?> addVoter(@RequestBody Map<String, Object> voter) {
        return ResponseEntity.ok(voter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoter(@PathVariable Long id, @RequestBody Map<String, Object> voter) {
        return ResponseEntity.ok(voter);
    }
}
