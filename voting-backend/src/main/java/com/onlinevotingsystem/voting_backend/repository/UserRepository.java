package com.onlinevotingsystem.voting_backend.repository;

import com.onlinevotingsystem.voting_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByVoterId(String voterId);
    boolean existsByVoterId(String voterId);

    List<User> findByStatus(String status);

    long countByStatus(String status);
}
