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
@RequestMapping("/api/elections")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ElectionController {

    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final VoterService voterService;

    public ElectionController(ElectionService electionService, CandidateService candidateService, VoterService voterService) {
        this.electionService = electionService;
        this.candidateService = candidateService;
        this.voterService = voterService;
    }

    // Get all elections (for users)
    @GetMapping
    public ResponseEntity<?> getAllElections() {
        try {
            List<Election> elections = electionService.findAll();
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching elections");
        }
    }

    // Get active elections
    @GetMapping("/active")
    public ResponseEntity<?> getActiveElections() {
        try {
            List<Election> elections = electionService.findByStatus("Active");
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching active elections");
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
            return ResponseEntity.status(500).body("Error fetching election");
        }
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> getPublicResults(@PathVariable Long id) {
        try {
            Election election = electionService.findById(id);
            if (election == null) {
                return ResponseEntity.status(404).body("Election not found");
            }

            // Check if results are declared to public
            if (!"Completed".equalsIgnoreCase(election.getStatus())) {
                return ResponseEntity.badRequest().body("Results not yet available - election not completed");
            }

            if (!Boolean.TRUE.equals(election.getResultsDeclared())) {
                return ResponseEntity.badRequest().body("Results not yet declared by election officials");
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
}