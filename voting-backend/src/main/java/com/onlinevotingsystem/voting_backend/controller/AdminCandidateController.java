package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import com.onlinevotingsystem.voting_backend.service.CandidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/candidates")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminCandidateController {
    private final CandidateService candidateService;

    public AdminCandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    public ResponseEntity<?> getCandidates(@RequestParam(required = false) Long electionId) {
        if (electionId != null) {
            return ResponseEntity.ok(candidateService.getCandidatesByElection(electionId));
        }
        return ResponseEntity.ok(candidateService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> addCandidate(@RequestBody Candidate candidate) {
        return ResponseEntity.ok(candidateService.save(candidate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable Long id, @RequestBody Candidate candidate) {
        candidate.setId(id);
        return ResponseEntity.ok(candidateService.save(candidate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Candidate deleted"));
    }
}
