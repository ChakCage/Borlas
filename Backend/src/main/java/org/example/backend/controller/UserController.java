package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.*;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;

    /* ---------- CREATE (admin) ---------- */
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserDto dto) {
        if (repo.existsByEmail(dto.getEmail())) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        User saved = repo.save(UserMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(saved));
    }

    /* ---------- SIGN‑UP (public) -------- */
    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSignupDto dto) {
        if (repo.existsByEmail(dto.getEmail()) || repo.existsByUsername(dto.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        User saved = repo.save(UserMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(saved));
    }

    /* ---------- READ ALL ---------------- */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        var list = repo.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /* ---------- READ BY ID -------------- */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return repo.findById(id)
                .map(u -> ResponseEntity.ok(UserMapper.toDto(u)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /* ---------- PUT full update --------- */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UserDto dto) {
        return repo.findById(id)
                .map(u -> {
                    UserMapper.update(u, dto);
                    return ResponseEntity.ok(UserMapper.toDto(repo.save(u)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /* ---------- PATCH own profile ------- */
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> patchMe(@AuthenticationPrincipal UserDetails ud,
                                                   @Valid @RequestBody UserPatchDto dto) {
        User u = repo.findByUsername(ud.getUsername()).orElseThrow();
        UserMapper.patch(u, dto);
        return ResponseEntity.ok(UserMapper.toDto(repo.save(u)));
    }

    /* ---------- DELETE ------------------ */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (repo.existsById(id)) { repo.deleteById(id); return ResponseEntity.noContent().build(); }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
