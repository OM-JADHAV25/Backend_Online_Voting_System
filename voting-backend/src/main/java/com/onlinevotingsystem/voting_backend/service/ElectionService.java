package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.repository.CandidateRepository;
import com.onlinevotingsystem.voting_backend.repository.ElectionRepository;
import com.onlinevotingsystem.voting_backend.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateService candidateService;
    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;

    public ElectionService(ElectionRepository electionRepository,
                           CandidateService candidateService,
                           VoteRepository voteRepository,
                           CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.candidateService = candidateService;
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
    }


    // Method for VoteController (returns Optional)
    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }

    // Method for Admin controllers (returns Election or null)
    public Election findById(Long id) {
        Election election = electionRepository.findById(id).orElse(null);
        if (election != null) {
            updateElectionStatus(election);
        }
        return election;
    }

    public List<Election> findAll() {
        List<Election> elections = electionRepository.findAll();
        elections.forEach(this::updateElectionStatus);
        return elections;
    }

    public Election save(Election election) {
        return electionRepository.save(election);
    }

    public void deleteById(Long id) {
        electionRepository.deleteById(id);
    }

    public List<Election> findByStatus(String status) {
        List<Election> elections = electionRepository.findAll();
        elections.forEach(this::updateElectionStatus);
        // Filter by actual computed status
        return elections.stream()
                .filter(e -> e.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    // Stop election by setting a 'status' field
    public void stopElection(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found!"));
        election.setStatus("STOPPED");
        electionRepository.save(election);
    }

    // Helper method to dynamically update status based on dates
    private void updateElectionStatus(Election election) {
        // Don't override manually set "Completed" status
        if ("Completed".equalsIgnoreCase(election.getStatus())) {
            return; // Keep as Completed if manually stopped
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = election.getStartDate();
        LocalDate endDate = election.getEndDate();

        if (startDate != null && endDate != null) {
            if (today.isBefore(startDate)) {
                election.setStatus("Upcoming");
            } else if (today.isAfter(endDate)) {
                election.setStatus("Completed");
            } else {
                election.setStatus("Active");
            }
        }
    }

    @Transactional
    public Map<String, Object> stopElectionAndCalculateResults(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        System.out.println("Stopping election: " + election.getName() + " (ID: " + electionId + ")");
        System.out.println("Current status: " + election.getStatus());

        // Change status to Completed
        election.setStatus("Completed");

        // IMPORTANT: Save immediately to persist status change
        election = electionRepository.save(election);

        System.out.println("Election status updated to: " + election.getStatus());

        // Get all candidates and their votes
        List<Candidate> candidates = candidateService.getCandidatesByElection(electionId);

        if (candidates.isEmpty()) {
            throw new RuntimeException("No candidates found for this election");
        }

        // Sort candidates by votes (descending)
        candidates.sort((c1, c2) -> {
            int votes1 = c1.getVotes() != null ? c1.getVotes() : 0;
            int votes2 = c2.getVotes() != null ? c2.getVotes() : 0;
            return Integer.compare(votes2, votes1);
        });

        // Calculate total votes
        int totalVotes = election.getTotalVotes() != null ? election.getTotalVotes() : 0;

        System.out.println("Total votes: " + totalVotes);
        System.out.println("Winner: " + candidates.get(0).getName() + " with " + candidates.get(0).getVotes() + " votes");

        // Prepare results
        Map<String, Object> results = new HashMap<>();
        results.put("electionId", electionId);
        results.put("electionName", election.getName());
        results.put("totalVotes", totalVotes);
        results.put("status", "Completed");

        // Winner details
        Candidate winner = candidates.get(0);
        int winnerVotes = winner.getVotes() != null ? winner.getVotes() : 0;
        double winnerPercentage = totalVotes > 0 ? (winnerVotes * 100.0 / totalVotes) : 0;

        Map<String, Object> winnerData = new HashMap<>();
        winnerData.put("name", winner.getName());
        winnerData.put("party", winner.getParty());
        winnerData.put("votes", winnerVotes);
        winnerData.put("percentage", String.format("%.2f", winnerPercentage));

        results.put("winner", winnerData);

        // All candidates breakdown
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

        return results;
    }

    @Transactional
    public void deleteElectionWithRelatedData(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        // Delete all votes for this election
        voteRepository.deleteAll(
                voteRepository.findAll().stream()
                        .filter(vote -> vote.getElection().getId().equals(electionId))
                        .toList()
        );

        // Delete all candidates for this election
        candidateRepository.deleteAll(
                candidateRepository.findAll().stream()
                        .filter(candidate -> candidate.getElection() != null &&
                                candidate.getElection().getId().equals(electionId))
                        .toList()
        );

        // Finally delete the election
        electionRepository.deleteById(electionId);
    }
}
