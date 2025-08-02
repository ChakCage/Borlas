package org.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.*;
import java.util.*;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="username", nullable = false, length = 30, unique = true)
    private String username;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name="bio", length = 4096)
    private String bio;

    @Column(name="avatar_url", length = 1024)
    private String avatarUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
