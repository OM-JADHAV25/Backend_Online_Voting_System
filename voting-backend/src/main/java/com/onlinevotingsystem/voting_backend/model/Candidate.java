package com.onlinevotingsystem.voting_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String party;

    @Column(name = "votes")
    private Integer votes = 0;

    public Integer getVotes() { return votes; }
    public void setVotes(Integer votes) { this.votes = votes; }

    @Column(name = "election_id", insertable = false, updatable = false)
    private Long electionId;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    // Getter/setter for electionId
    public Long getElectionId() {
        return electionId;
    }

    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }

    @Column(columnDefinition = "LONGTEXT")  // Allow storing base64 images
    private String photo;
}
