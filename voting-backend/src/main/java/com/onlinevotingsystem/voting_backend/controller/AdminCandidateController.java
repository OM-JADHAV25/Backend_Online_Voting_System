package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.service.CandidateService;
import com.onlinevotingsystem.voting_backend.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/candidates")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminCandidateController {
    private final CandidateService candidateService;
    private final ElectionService electionService;

    public AdminCandidateController(CandidateService candidateService, ElectionService electionService) {
        this.candidateService = candidateService;
        this.electionService = electionService;
    }

    @GetMapping
    public ResponseEntity<?> getCandidates(@RequestParam(required = false) Long electionId) {
        try {
            if (electionId != null) {
                return ResponseEntity.ok(candidateService.getCandidatesByElection(electionId));
            }
            return ResponseEntity.ok(candidateService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addCandidate(@RequestBody Candidate candidate) {
        try {
            // If no photo provided, set default avatar path
            if (candidate.getPhoto() == null || candidate.getPhoto().trim().isEmpty()) {
                candidate.setPhoto("/assets/defaultAvatar.jpg"); // Or your default path
            }

            // Fetch the election and set it on the candidate
            if (candidate.getElectionId() != null) {
                Election election = electionService.findById(candidate.getElectionId());
                if (election == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Election not found"));
                }
                candidate.setElection(election);
            }
            return ResponseEntity.ok(candidateService.save(candidate));
        } catch (Exception e) {
            e.printStackTrace(); // This will show the actual error in logs
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable Long id, @RequestBody Candidate candidate) {
        try {
            candidate.setId(id);
            // Fetch and set the election
            if (candidate.getElectionId() != null) {
                Election election = electionService.findById(candidate.getElectionId());
                if (election == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Election not found"));
                }
                candidate.setElection(election);
            }
            return ResponseEntity.ok(candidateService.save(candidate));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Candidate deleted"));
    }
}
