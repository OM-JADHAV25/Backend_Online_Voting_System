package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.model.Vote;
import com.onlinevotingsystem.voting_backend.security.JwtUtil;
import com.onlinevotingsystem.voting_backend.service.CandidateService;
import com.onlinevotingsystem.voting_backend.service.ElectionService;
import com.onlinevotingsystem.voting_backend.service.UserService;
import com.onlinevotingsystem.voting_backend.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;
    private final UserService userService;
    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final JwtUtil jwtUtil;

    public VoteController(VoteService voteService,
                          UserService userService,
                          ElectionService electionService,
                          CandidateService candidateService,
                          JwtUtil jwtUtil) {
        this.voteService = voteService;
        this.userService = userService;
        this.electionService = electionService;
        this.candidateService = candidateService;
        this.jwtUtil = jwtUtil;
    }

    // Cast vote using JWT authentication
    @PostMapping("/cast")
    public ResponseEntity<?> voteCast(@RequestHeader("Authorization") String authHeader,
                                      @RequestParam Long electionId,
                                      @RequestParam Long candidateId) {

        try {
            // Extract voter ID from JWT
            String token = authHeader.replace("Bearer ", "");
            String voterId = jwtUtil.extractVoterId(token);

            // Fetch voter and validate existence
            User user = userService.getUserDetails(voterId);

            // Fetch election and validate it exists
            Election election = electionService.getElectionById(electionId)
                    .orElseThrow(() -> new RuntimeException("Election not found!"));

            // Ensure election is ongoing - FIXED STATUS CHECK
            if (!"Active".equalsIgnoreCase(election.getStatus())) {
                return ResponseEntity.badRequest().body("Election is not active! Current status: " + election.getStatus());
            }

            // Check if voter has already voted
            if (voteService.hasUserVoted(user, election)) {
                return ResponseEntity.badRequest().body("User has already voted in this election!");
            }

            // Fetch candidate and validate
            Candidate candidate = candidateService.getCandidatesByElection(electionId).stream()
                    .filter(c -> c.getId().equals(candidateId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Candidate not found!"));

            // Cast the vote
            Vote vote = voteService.castVote(user, election, candidate);

            return ResponseEntity.ok(vote);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get election results
    @GetMapping("/results/{electionId}")
    public ResponseEntity<?> getResults(@PathVariable Long electionId) {
        try {
            Election election = electionService.getElectionById(electionId)
                    .orElseThrow(() -> new RuntimeException("Election not found!"));

            List<Candidate> candidates = candidateService.getCandidatesByElection(electionId);

            Map<String, Object> results = new HashMap<>();
            results.put("electionId", electionId);
            results.put("electionName", election.getName());

            Map<String, Long> candidateVotes = new HashMap<>();
            for (Candidate candidate : candidates) {
                long votes = voteService.getVoteCount(candidate);
                candidateVotes.put(candidate.getName(), votes);
            }

            results.put("results", candidateVotes);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}