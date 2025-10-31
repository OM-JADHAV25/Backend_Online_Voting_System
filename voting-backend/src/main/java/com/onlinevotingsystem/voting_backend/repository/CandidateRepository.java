package com.onlinevotingsystem.voting_backend.repository;

import com.onlinevotingsystem.voting_backend.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByElectionId(Long electionId);
    List<Candidate> findByParty(String party);

}
