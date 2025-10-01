package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.Admin;
import com.onlinevotingsystem.voting_backend.repository.AdminRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;



@Service
public class AdminService {

    // Hardcoded admin credentials
    private final Map<String, String> adminCredentials = new HashMap<>();

    public AdminService() {
        // Initialize with your hardcoded admin credentials
        adminCredentials.put("admin001", "admin123");
        adminCredentials.put("admin002", "admin456");
        // Add more admins if needed
    }

    public boolean authenticateAdmin(String adminId, String password) {
        String storedPassword = adminCredentials.get(adminId);
        return storedPassword != null && storedPassword.equals(password);
    }

    public Map<String, Object> getAdminDetails(String adminId) {
        if (adminCredentials.containsKey(adminId)) {
            Map<String, Object> adminDetails = new HashMap<>();
            adminDetails.put("id", adminId);
            adminDetails.put("name", "System Administrator"); // You can customize this
            adminDetails.put("role", "ADMIN");
            return adminDetails;
        }
        return null;
    }
}
