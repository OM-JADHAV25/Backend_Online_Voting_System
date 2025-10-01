package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
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
}