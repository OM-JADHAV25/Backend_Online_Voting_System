package com.onlinevotingsystem.voting_backend.scheduler;

import com.onlinevotingsystem.voting_backend.model.Election;
import com.onlinevotingsystem.voting_backend.repository.ElectionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Component
public class ElectionStatusScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ElectionStatusScheduler.class);
    private final ElectionRepository electionRepository;

    public ElectionStatusScheduler(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    // Run every 5 minutes (300000 milliseconds)
    // For production: use @Scheduled(cron = "0 0 * * * *") for every hour
    @Scheduled(fixedRate = 300000)
    public void updateElectionStatuses() {
        LocalDate today = LocalDate.now();
        List<Election> elections = electionRepository.findAll();

        int updated = 0;
        for (Election election : elections) {
            String oldStatus = election.getStatus();
            String newStatus = calculateStatus(election, today);

            if (!newStatus.equals(oldStatus)) {
                election.setStatus(newStatus);
                electionRepository.save(election);
                updated++;
                logger.info("Election '{}' status updated: {} -> {}",
                        election.getName(), oldStatus, newStatus);
            }
        }

        if (updated > 0) {
            logger.info("Updated {} election statuses", updated);
        }
    }

    // Also run on application startup
    @Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE)
    public void updateStatusesOnStartup() {
        logger.info("Running initial election status update...");
        updateElectionStatuses();
    }

    private String calculateStatus(Election election, LocalDate today) {
        // Don't override manually completed elections
        if ("Completed".equalsIgnoreCase(election.getStatus())) {
            return "Completed"; // Keep manually stopped elections as Completed
        }

        if (election.getStartDate() != null && election.getEndDate() != null) {
            if (today.isBefore(election.getStartDate())) {
                return "Upcoming";
            } else if (today.isAfter(election.getEndDate())) {
                return "Completed";
            } else {
                return "Active";
            }
        }
        return election.getStatus();
    }
}
