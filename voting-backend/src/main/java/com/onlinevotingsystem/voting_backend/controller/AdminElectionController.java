package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/elections")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminElectionController {

    private final ElectionService electionService;

    public AdminElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    // Get all elections
    @GetMapping
    public ResponseEntity<?> getAllElections() {
        try {
            List<Election> elections = electionService.findAll();
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching elections",
                    "error", e.getMessage()
            ));
        }
    }

    // Get election by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getElectionById(@PathVariable Long id) {
        try {
            Election election = electionService.findById(id);
            if (election != null) {
                return ResponseEntity.ok(election);
            } else {
                return ResponseEntity.status(404).body("Election not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching election",
                    "error", e.getMessage()
            ));
        }
    }

    // Create new election
    @PostMapping
    public ResponseEntity<?> createElection(@RequestBody Election election) {
        try {
            // Set default status if not provided
            if (election.getStatus() == null) {
                election.setStatus("Upcoming");
            }
            Election savedElection = electionService.save(election);
            return ResponseEntity.ok(savedElection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error creating election",
                    "error", e.getMessage()
            ));
        }
    }

    // Update election
    @PutMapping("/{id}")
    public ResponseEntity<?> updateElection(@PathVariable Long id, @RequestBody Election election) {
        try {
            Election existingElection = electionService.findById(id);
            if (existingElection == null) {
                return ResponseEntity.status(404).body("Election not found");
            }

            // Update fields
            existingElection.setName(election.getName());
            existingElection.setType(election.getType());
            existingElection.setDistrict(election.getDistrict());
            existingElection.setStartDate(election.getStartDate());
            existingElection.setEndDate(election.getEndDate());
            existingElection.setDescription(election.getDescription());
            existingElection.setStatus(election.getStatus());

            Election updatedElection = electionService.save(existingElection);
            return ResponseEntity.ok(updatedElection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error updating election",
                    "error", e.getMessage()
            ));
        }
    }

    // Delete election
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable Long id) {
        try {
            Election election = electionService.findById(id);
            if (election == null) {
                return ResponseEntity.status(404).body("Election not found");
            }

            electionService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Election deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error deleting election",
                    "error", e.getMessage()
            ));
        }
    }

    // Get elections by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getElectionsByStatus(@PathVariable String status) {
        try {
            List<Election> elections = electionService.findByStatus(status);
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching elections by status",
                    "error", e.getMessage()
            ));
        }
    }

    // Get dashboard statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            // Mock data - replace with actual service calls
            Map<String, Object> stats = Map.of(
                    "totalVoters", 1500,
                    "votesCast", 1200,
                    "activeElections", 3,
                    "pendingVoters", 25,
                    "turnoutRate", 80,
                    "weeklyGrowth", 12
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching dashboard stats",
                    "error", e.getMessage()
            ));
        }
    }

    // Get recent activity
    @GetMapping("/dashboard/recent-activity")
    public ResponseEntity<?> getRecentActivity() {
        try {
            // Mock data - replace with actual service calls
            List<Map<String, String>> activities = List.of(
                    Map.of("id", "1", "description", "New voter registered: John Doe", "timestamp", "2 minutes ago"),
                    Map.of("id", "2", "description", "Election 'Student Council 2024' created", "timestamp", "1 hour ago"),
                    Map.of("id", "3", "description", "Candidate Alice Johnson added", "timestamp", "3 hours ago"),
                    Map.of("id", "4", "description", "Voting started for 'Class Representative'", "timestamp", "5 hours ago")
            );
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching recent activity",
                    "error", e.getMessage()
            ));
        }
    }
}