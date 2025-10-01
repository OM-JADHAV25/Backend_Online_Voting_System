package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.repository.ElectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    // Method for VoteController (returns Optional)
    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }

    // Method for Admin controllers (returns Election or null)
    public Election findById(Long id) {
        Optional<Election> election = electionRepository.findById(id);
        return election.orElse(null);
    }

    public List<Election> findAll() {
        return electionRepository.findAll();
    }

    public Election save(Election election) {
        return electionRepository.save(election);
    }

    public void deleteById(Long id) {
        electionRepository.deleteById(id);
    }

    public List<Election> findByStatus(String status) {
        return electionRepository.findByStatus(status);
    }

    // Stop election by setting a 'status' field
    public void stopElection(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found!"));
        election.setStatus("STOPPED");
        electionRepository.save(election);
    }



}
