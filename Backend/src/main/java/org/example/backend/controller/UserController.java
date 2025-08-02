package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.UserRequestDto;
import org.example.backend.dto.UserResponseDto;
import org.example.backend.exception.ConflictException;
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


    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto dto) {
        if (repo.existsByEmail(dto.getEmail()))
            throw new ConflictException("Этот email уже зарегистрирован");
        if (repo.existsByUsername(dto.getUsername()))
            throw new ConflictException("Этот username уже занят");

        User saved = repo.save(UserMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<UserResponseDto> list = repo.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return repo.findById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toDto(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UserRequestDto userRequestDto) {
        return repo.findById(id)
                .map(user -> {
                    UserMapper.update(user, userRequestDto);
                    return ResponseEntity.ok(UserMapper.toDto(repo.save(user)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PatchMapping("/me") //??? для чего этот метод ???
    public ResponseEntity<UserResponseDto> patchMe(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody UserRequestDto userRequestDto) {
        User user = repo.findByUsername(userDetails.getUsername()).orElseThrow();
        UserMapper.patch(user, userRequestDto);
        return ResponseEntity.ok(UserMapper.toDto(repo.save(user)));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.ok(String.format("User with id: %s | Successfully Deleted", id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
