package com.onlinevotingsystem.voting_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role;

    private String constituency;

    private LocalDateTime registrationDate;

    @Column(unique = true)
    private String voterId;

    private String otp;

    private LocalDateTime otpExpires;
}
