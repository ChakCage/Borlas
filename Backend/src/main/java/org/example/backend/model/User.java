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

    /* ───────── обязательные ───────── */
    @Column(nullable = false, length = 30, unique = true)
    private String username;                 // 3‑30 символов a‑z,0‑9,_‑

    @Column(nullable = false, unique = true)
    private String email;                    // валидный e‑mail

    @Column(nullable = false, length = 100)
    private String passwordHash;             // хранится BCrypt‑хеш

    /* ───────── необязательные ─────── */
    @Column(length = 500)
    private String bio;                      // ≤ 500 симв.

    private String avatarUrl;                // ссылка/путь к файлу

    private LocalDate birthDate;             // дата в прошлом

    @Enumerated(EnumType.STRING)
    private Gender gender;                   // MALE | FEMALE

    /* ───────── системные ──────────── */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ───────── связи ──────────────── */
    @OneToMany(mappedBy = "author",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
}
