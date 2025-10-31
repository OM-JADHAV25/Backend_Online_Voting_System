package com.onlinevotingsystem.voting_backend.repository;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.model.VotingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingHistoryRepository extends JpaRepository<VotingHistory, Long> {
    List<VotingHistory> findByUserOrderByVotedAtDesc(User user);
}