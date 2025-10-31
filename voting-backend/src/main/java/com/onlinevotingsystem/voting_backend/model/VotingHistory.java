package com.onlinevotingsystem.voting_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voting_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Store election details as strings (permanent record)
    @Column(name = "election_name")
    private String electionName;

    @Column(name = "election_type")
    private String electionType;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "candidate_party")
    private String candidateParty;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;

    @Column(name = "reference_number")
    private String referenceNumber; // Unique reference for each vote

    private String constituency;
}
