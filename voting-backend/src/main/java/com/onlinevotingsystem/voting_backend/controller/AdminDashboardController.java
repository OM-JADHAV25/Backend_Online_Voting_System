package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminDashboardController {
    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            return ResponseEntity.ok(dashboardService.getDashboardStats());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching dashboard stats",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<?> getRecentActivity() {
        try {
            List<Map<String, String>> activities = List.of(
                    Map.of("id", "1", "description", "New voter registered: John Doe", "timestamp", "2 minutes ago"),
                    Map.of("id", "2", "description", "Election 'Student Council 2024' created", "timestamp", "1 hour ago"),
                    Map.of("id", "3", "description", "Candidate Alice Johnson added", "timestamp", "3 hours ago"),
                    Map.of("id", "4", "description", "Voting started for 'Class Representative'", "timestamp", "5 hours ago")
            );
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching recent activity",
                    "error", e.getMessage()
            ));
        }
    }
}
