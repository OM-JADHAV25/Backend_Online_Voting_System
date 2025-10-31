package com.onlinevotingsystem.voting_backend.repository;

import com.onlinevotingsystem.voting_backend.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findByStatus(String status);
    List<Election> findByType(String type);
    List<Election> findByDistrict(String district);
    long countByStatus(String status);

}