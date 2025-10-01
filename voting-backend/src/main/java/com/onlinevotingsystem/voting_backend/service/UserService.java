package com.onlinevotingsystem.voting_backend.service;

import com.onlinevotingsystem.voting_backend.model.User;
import com.onlinevotingsystem.voting_backend.repository.UserRepository;
import com.onlinevotingsystem.voting_backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Register user
    public User registerUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already registered!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Authenticate user
    public Optional<User> authenticateUser(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Get user by Email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ------------------ OTP METHODS ------------------

    public void requestOtp(String voterId) throws Exception {
        User user = userRepository.findByVoterId(voterId)
                .orElseThrow(() -> new Exception("Voter ID not found. Please verify your voter ID and try again."));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        user.setOtp(otp);
        user.setOtpExpires(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // TODO: Send OTP via SMS or Email
        System.out.println("========================================");
        System.out.println("OTP Generated for Voter ID: " + voterId);
        System.out.println("OTP: " + otp);
        System.out.println("Valid until: " + user.getOtpExpires());
        System.out.println("========================================");
    }

    public String verifyOtp(String voterId, String otp) throws Exception {
        User user = userRepository.findByVoterId(voterId)
                .orElseThrow(() -> new Exception("Voter ID not found"));

        if(user.getOtp() == null || user.getOtpExpires() == null) {
            throw new Exception("No OTP requested. Please request OTP first.");
        }

        if(!otp.equals(user.getOtp())) {
            throw new Exception("Invalid OTP. Please check and try again.");
        }

        if(user.getOtpExpires().isBefore(LocalDateTime.now())) {
            throw new Exception("OTP has expired. Please request a new OTP.");
        }

        // Clear OTP after successful verification
        user.setOtp(null);
        user.setOtpExpires(null);
        userRepository.save(user);

        System.out.println("✓ OTP verified successfully for Voter ID: " + voterId);

        // Generate JWT token
        return jwtUtil.generateToken(voterId);
    }

    public User getUserDetails(String voterId) throws Exception {
        return userRepository.findByVoterId(voterId)
                .orElseThrow(() -> new Exception("Voter ID not found"));
    }
}