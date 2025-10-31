package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;

    public DashboardService(UserRepository userRepository,
                            ElectionRepository electionRepository,
                            VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.electionRepository = electionRepository;
        this.voteRepository = voteRepository;
    }

    public Map<String, Object> getDashboardStats() {
        long totalVoters = userRepository.count();
        long votesCast = voteRepository.count();
        long activeElections = electionRepository.countByStatus("Active");
        long pendingVoters = userRepository.countByStatus("Pending");

        double turnoutRate = totalVoters > 0 ? (votesCast * 100.0 / totalVoters) : 0;

        return Map.of(
                "totalVoters", totalVoters,
                "votesCast", votesCast,
                "activeElections", activeElections,
                "pendingVoters", pendingVoters,
                "turnoutRate", Math.round(turnoutRate),
                "weeklyGrowth", 0 // Calculate this based on your logic
        );
    }
}
