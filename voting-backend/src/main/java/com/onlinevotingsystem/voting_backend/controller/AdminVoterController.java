package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.service.VoterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/voters")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminVoterController {
    private final VoterService voterService;

    public AdminVoterController(VoterService voterService) {
        this.voterService = voterService;
    }

    @GetMapping
    public ResponseEntity<?> getAllVoters(@RequestParam(required = false) String status) {
        try {
            List<User> voters = status != null && !status.equals("All")
                    ? voterService.getVotersByStatus(status)
                    : voterService.getAllVoters();
            return ResponseEntity.ok(voters);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveVoter(@PathVariable Long id) {
        try {
            User voter = voterService.approveVoter(id);
            return ResponseEntity.ok(Map.of("message", "Voter approved", "voter", voter));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoter(@PathVariable Long id) {
        try {
            voterService.deleteVoter(id);
            return ResponseEntity.ok(Map.of("message", "Voter deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addVoter(@RequestBody User voter) {
        try {
            User saved = voterService.saveVoter(voter);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoter(@PathVariable Long id, @RequestBody User voter) {
        try {
            voter.setId(id);
            User updated = voterService.saveVoter(voter);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}