package com.onlinevotingsystem.voting_backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "election")
public class Election {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type; // National, Municipal, Referendum, etc.

    private String district;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String description;

    private String status = "Upcoming"; // Upcoming, Active, Completed, Cancelled

    @Column(name = "total_votes")
    private Integer totalVotes = 0;

    @Column(name = "candidate_count")
    private Integer candidateCount = 0;

    @Column(name = "results_declared")
    private Boolean resultsDeclared = false;


    // Constructors
    public Election() {}

    public Election(String name, String type, String district, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.type = type;
        this.district = district;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters

    public Boolean getResultsDeclared() { return resultsDeclared; }
    public void setResultsDeclared(Boolean resultsDeclared) { this.resultsDeclared = resultsDeclared; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalVotes() { return totalVotes; }
    public void setTotalVotes(Integer totalVotes) { this.totalVotes = totalVotes; }

    public Integer getCandidateCount() { return candidateCount; }
    public void setCandidateCount(Integer candidateCount) { this.candidateCount = candidateCount; }
}