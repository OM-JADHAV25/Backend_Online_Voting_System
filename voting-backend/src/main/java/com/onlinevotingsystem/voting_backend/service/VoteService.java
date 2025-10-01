package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.model.Vote;
import com.onlinevotingsystem.voting_backend.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    // Cast Vote
    public Vote castVote(User user, Election election, Candidate candidate) {
        // Check if election is ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(election.getStatus())) {
            throw new RuntimeException("Cannot vote in a stopped or inactive election");
        }

        // Check if user already voted
        if (voteRepository.existsByUserAndElection(user, election)) {
            throw new RuntimeException("User has already voted in this election");
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setElection(election);
        vote.setCandidate(candidate);
        vote.setTimestamp(LocalDateTime.now());

        return voteRepository.save(vote);
    }

    // Get vote count by candidate
    public long getVoteCount(Candidate candidate) {
        return voteRepository.countByCandidate(candidate);
    }

    // Get all votes in an election
    public List<Vote> getVotesByElection(Election election) {
        return voteRepository.findAll().stream()
                .filter(v -> v.getElection().equals(election))
                .toList();
    }

    public boolean hasUserVoted(User user, Election election) {
        return voteRepository.existsByUserAndElection(user, election);
    }
}
