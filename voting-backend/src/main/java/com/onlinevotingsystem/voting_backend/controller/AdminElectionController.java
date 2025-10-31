package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.service.CandidateService;
import com.onlinevotingsystem.voting_backend.service.ElectionService;
import com.onlinevotingsystem.voting_backend.service.VoterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/elections")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminElectionController {

    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final VoterService voterService;

    public AdminElectionController(ElectionService electionService, CandidateService candidateService, VoterService voterService) {
        this.electionService = electionService;
        this.candidateService = candidateService;
        this.voterService = voterService;

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
        System.out.println("========== DELETE ELECTION REQUEST ==========");
        System.out.println("Attempting to delete election ID: " + id);

        try {
            Election election = electionService.findById(id);

            if (election == null) {
                System.out.println("ERROR: Election not found");
                return ResponseEntity.status(404).body(Map.of("error", "Election not found"));
            }

            System.out.println("Found election: " + election.getName());
            System.out.println("Election status: " + election.getStatus());

            // Delete election regardless of status (for testing)
            System.out.println("Calling deleteElectionWithRelatedData...");
            electionService.deleteElectionWithRelatedData(id);

            System.out.println("SUCCESS: Election deleted");
            return ResponseEntity.ok(Map.of("message", "Election deleted successfully"));

        } catch (Exception e) {
            System.err.println("========== DELETE ERROR ==========");
            e.printStackTrace();
            System.err.println("===================================");

            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error deleting election: " + e.getMessage()
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

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopElection(@PathVariable Long id) {
        try {
            Map<String, Object> results = electionService.stopElectionAndCalculateResults(id);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error stopping election",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> getElectionResults(@PathVariable Long id) {
        try {
            Election election = electionService.findById(id);
            if (election == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Election not found"));
            }

            if (!"Completed".equalsIgnoreCase(election.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Election is not completed yet"));
            }

            List<Candidate> candidates = candidateService.getCandidatesByElection(id);
            candidates.sort((c1, c2) -> {
                int votes1 = c1.getVotes() != null ? c1.getVotes() : 0;
                int votes2 = c2.getVotes() != null ? c2.getVotes() : 0;
                return Integer.compare(votes2, votes1);
            });

            int totalVotes = election.getTotalVotes() != null ? election.getTotalVotes() : 0;

            // Calculate actual turnout based on registered voters
            long totalRegisteredVoters = voterService.countTotalRegisteredVoters();
            double turnoutPercentage = totalRegisteredVoters > 0
                    ? (totalVotes * 100.0 / totalRegisteredVoters)
                    : 0;

            Map<String, Object> results = new HashMap<>();
            results.put("electionId", id);
            results.put("electionName", election.getName());
            results.put("totalVotes", totalVotes);
            results.put("totalRegisteredVoters", totalRegisteredVoters); // ADD THIS
            results.put("turnout", String.format("%.2f", turnoutPercentage)); // Real turnout
            results.put("status", election.getStatus());
            results.put("resultsDeclared", election.getResultsDeclared());

            if (!candidates.isEmpty()) {
                Candidate winner = candidates.get(0);
                int winnerVotes = winner.getVotes() != null ? winner.getVotes() : 0;
                double winnerPercentage = totalVotes > 0 ? (winnerVotes * 100.0 / totalVotes) : 0;

                results.put("winner", Map.of(
                        "name", winner.getName(),
                        "party", winner.getParty(),
                        "votes", winnerVotes,
                        "percentage", String.format("%.2f", winnerPercentage)
                ));

                // Calculate margin of victory
                if (candidates.size() > 1) {
                    Candidate runnerUp = candidates.get(1);
                    int runnerUpVotes = runnerUp.getVotes() != null ? runnerUp.getVotes() : 0;
                    int margin = winnerVotes - runnerUpVotes;
                    results.put("marginOfVictory", margin);
                } else {
                    results.put("marginOfVictory", winnerVotes); // Only one candidate
                }
            } else {
                results.put("winner", null);
                results.put("marginOfVictory", 0);
            }

            List<Map<String, Object>> breakdown = new ArrayList<>();
            for (Candidate candidate : candidates) {
                int votes = candidate.getVotes() != null ? candidate.getVotes() : 0;
                double percentage = totalVotes > 0 ? (votes * 100.0 / totalVotes) : 0;

                Map<String, Object> candidateData = new HashMap<>();
                candidateData.put("id", candidate.getId());
                candidateData.put("name", candidate.getName());
                candidateData.put("party", candidate.getParty());
                candidateData.put("votes", votes);
                candidateData.put("percentage", String.format("%.2f", percentage));
                breakdown.add(candidateData);
            }
            results.put("breakdown", breakdown);

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error fetching results: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/declare-results")
    public ResponseEntity<?> declareResults(@PathVariable Long id) {
        try {
            Election election = electionService.findById(id);
            if (election == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Election not found"));
            }

            if (!"Completed".equalsIgnoreCase(election.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Can only declare results for completed elections"));
            }

            System.out.println("Declaring results for election: " + election.getName());

            election.setResultsDeclared(true);
            electionService.save(election);

            System.out.println("Results declared successfully");

            return ResponseEntity.ok(Map.of(
                    "message", "Results declared successfully",
                    "electionId", id,
                    "electionName", election.getName()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error declaring results: " + e.getMessage()
            ));
        }
    }
    }