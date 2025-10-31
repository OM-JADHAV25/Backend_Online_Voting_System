package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.*;
import com.onlinevotingsystem.voting_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final VotingHistoryRepository votingHistoryRepository;  // ADD THIS
    private final UserRepository userRepository;  // ADD THIS

    public VoteService(VoteRepository voteRepository,
                       CandidateRepository candidateRepository,
                       ElectionRepository electionRepository,
                       VotingHistoryRepository votingHistoryRepository,  // ADD THIS
                       UserRepository userRepository) {  // ADD THIS
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
        this.votingHistoryRepository = votingHistoryRepository;  // ADD THIS
        this.userRepository = userRepository;  // ADD THIS
    }

    @Transactional
    public Vote castVote(User user, Election election, Candidate candidate) {
        // Check if election is ACTIVE
        if (!"Active".equalsIgnoreCase(election.getStatus())) {
            throw new RuntimeException("Cannot vote in an inactive election");
        }

        // Check if user is approved
        if (!"Approved".equalsIgnoreCase(user.getStatus()) &&
                !"Verified".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Only approved voters can cast votes. Your status: " + user.getStatus());
        }

        // Check if user already voted IN THIS SPECIFIC ELECTION
        if (voteRepository.existsByUserAndElection(user, election)) {
            throw new RuntimeException("You have already voted in this election");
        }

        LocalDateTime now = LocalDateTime.now();

        // Create and save vote
        Vote vote = new Vote();
        vote.setUser(user);
        vote.setElection(election);
        vote.setCandidate(candidate);
        vote.setTimestamp(now);
        Vote savedVote = voteRepository.save(vote);

        // Increment candidate vote count
        candidate.setVotes((candidate.getVotes() != null ? candidate.getVotes() : 0) + 1);
        candidateRepository.save(candidate);

        // Increment election total votes
        election.setTotalVotes((election.getTotalVotes() != null ? election.getTotalVotes() : 0) + 1);
        electionRepository.save(election);

        // Update user's lastVoted timestamp
        user.setLastVoted(now);
        userRepository.save(user);

        // Create permanent voting history record
        VotingHistory history = new VotingHistory();
        history.setUser(user);
        history.setElectionName(election.getName());
        history.setElectionType(election.getType());
        history.setCandidateName(candidate.getName());
        history.setCandidateParty(candidate.getParty());
        history.setVotedAt(now);
        history.setConstituency(user.getConstituency());

        // Generate unique reference number
        String refNumber = "VR-" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        history.setReferenceNumber(refNumber);

        votingHistoryRepository.save(history);

        return savedVote;
    }

    public long getVoteCount(Candidate candidate) {
        return voteRepository.countByCandidate(candidate);
    }

    public List<Vote> getVotesByElection(Election election) {
        return voteRepository.findAll().stream()
                .filter(v -> v.getElection().equals(election))
                .toList();
    }

    public boolean hasUserVoted(User user, Election election) {
        return voteRepository.existsByUserAndElection(user, election);
    }

    public List<Long> getElectionsVotedByUser(User user) {
        return voteRepository.findAll().stream()
                .filter(v -> v.getUser().getId().equals(user.getId()))
                .map(v -> v.getElection().getId())
                .distinct()
                .toList();
    }

    // Get voting history for a user
    public List<VotingHistory> getVotingHistory(User user) {
        return votingHistoryRepository.findByUserOrderByVotedAtDesc(user);
    }
}