package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.repository.CandidateRepository;
import com.onlinevotingsystem.voting_backend.repository.ElectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;  // ADD THIS

    // UPDATE CONSTRUCTOR
    public CandidateService(CandidateRepository candidateRepository,
                            ElectionRepository electionRepository) {
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
    }

    public List<Candidate> getCandidatesByElection(Long electionId) {
        return candidateRepository.findByElectionId(electionId);
    }

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    // UPDATED save method
    public Candidate save(Candidate candidate) {
        boolean isNew = candidate.getId() == null;

        Candidate saved = candidateRepository.save(candidate);

        // Increment count for new candidates
        if (isNew && saved.getElection() != null) {
            Election election = saved.getElection();
            Integer currentCount = election.getCandidateCount() != null ? election.getCandidateCount() : 0;
            election.setCandidateCount(currentCount + 1);
            electionRepository.save(election);
        }

        return saved;
    }

    // UPDATED deleteById method
    public void deleteById(Long id) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);

        candidateRepository.deleteById(id);

        // Decrement count after deletion
        if (candidateOpt.isPresent() && candidateOpt.get().getElection() != null) {
            Election election = candidateOpt.get().getElection();
            Integer currentCount = election.getCandidateCount() != null ? election.getCandidateCount() : 0;
            election.setCandidateCount(Math.max(0, currentCount - 1));
            electionRepository.save(election);
        }
    }

    public Candidate addCandidates(Candidate candidate) {
        return save(candidate);  // Use the updated save method
    }

    // UPDATED deleteCandidate method
    public void deleteCandidate(Long candidateId) {
        deleteById(candidateId);  // Use the updated deleteById method
    }
}