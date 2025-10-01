package com.onlinevotingsystem.voting_backend.controller;

import com.onlinevotingsystem.voting_backend.security.JwtUtil;
import com.onlinevotingsystem.voting_backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminAuthController {
    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public AdminAuthController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Verify hardcoded credentials
            boolean isAuthenticated = adminService.authenticateAdmin(
                    loginRequest.getAdminId(),
                    loginRequest.getPassword()
            );

            if (!isAuthenticated) {
                return ResponseEntity.badRequest().body("Invalid admin credentials");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(loginRequest.getAdminId());

            // Get admin details
            Map<String, Object> adminDetails = adminService.getAdminDetails(loginRequest.getAdminId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("admin", adminDetails);
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String adminId = jwtUtil.extractVoterId(token);

                // Verify token is valid and adminId exists in our hardcoded list
                if (jwtUtil.validateToken(token, adminId) &&
                        adminService.getAdminDetails(adminId) != null) {

                    Map<String, Object> adminDetails = adminService.getAdminDetails(adminId);
                    return ResponseEntity.ok(adminDetails);
                }
            }
            return ResponseEntity.status(401).body("Invalid or expired token");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token verification failed");
        }
    }

    // Inner class for login request
    public static class LoginRequest {
        private String adminId;
        private String password;

        // getters and setters
        public String getAdminId() { return adminId; }
        public void setAdminId(String adminId) { this.adminId = adminId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
