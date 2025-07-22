// src/main/java/org/example/backend/controller/UserController.java
package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.*;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;

    /* ───────────────── create ───────────────── */
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserDto dto) {

        if (repo.existsByEmail(dto.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        User saved = repo.save(UserMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(saved));
    }

    /* ───────────────── read all ──────────────── */
    @GetMapping
    public List<UserResponseDto> getAll() {
        return repo.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    /* ───────────────── read by id ────────────── */
    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable UUID id) {
        return repo.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("user " + id + " not found"));
    }

    /* ───────────────── update ────────────────── */
    @PutMapping("/{id}")
    public UserResponseDto update(@PathVariable UUID id,
                                  @Valid @RequestBody UserDto dto) {

        User user = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("user " + id + " not found"));

        UserMapper.update(user, dto);
        return UserMapper.toDto(repo.save(user));
    }

    /* ───────────────── delete ────────────────── */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (repo.existsById(id)) repo.deleteById(id);
        else throw new NoSuchElementException("user " + id + " not found");
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSignupDto dto) {

        if (repo.existsByEmail(dto.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        if (repo.existsByUsername(dto.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        User saved = repo.save(UserMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(saved));
    }

    /* PATCH профиля */
    @PatchMapping("/users/me")
    public UserResponseDto patchMe(@AuthenticationPrincipal UserDetails userDet,
                                   @Valid @RequestBody UserSignupDto dto) {
        User user = repo.findByUsername(userDet.getUsername())
                .orElseThrow(); // никогда не null
        UserMapper.patch(user, dto);
        return UserMapper.toDto(repo.save(user));
    }
}
