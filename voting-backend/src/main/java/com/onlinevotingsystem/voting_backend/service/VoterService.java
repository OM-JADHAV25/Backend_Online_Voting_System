package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VoterService {
    private final UserRepository userRepository;

    public VoterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllVoters() {
        return userRepository.findAll();
    }

    public List<User> getVotersByStatus(String status) {
        // You'll need to add this method to UserRepository
        return userRepository.findByStatus(status);
    }

    public Optional<User> getVoterById(Long id) {
        return userRepository.findById(id);
    }

    public User saveVoter(User user) {
        return userRepository.save(user);
    }

    public void deleteVoter(Long id) {
        userRepository.deleteById(id);
    }

    public User approveVoter(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voter not found"));
        user.setStatus("Approved");  // Changed from "Verified" to "Approved"
        return userRepository.save(user);
    }

    public long countTotalRegisteredVoters() {
        return userRepository.count();
    }

    public long countVotersInConstituency(String constituency) {
        // If you want constituency-specific turnout
        return userRepository.findAll().stream()
                .filter(u -> constituency.equals(u.getConstituency()))
                .count();
    }
}
