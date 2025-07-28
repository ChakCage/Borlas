package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.PostRequest;
import org.example.backend.dto.PostResponse;
import org.example.backend.mapper.PostMapper;
import org.example.backend.model.User;
import org.example.backend.repository.PostRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest dto,
                                               @AuthenticationPrincipal UserDetails ud) {
        if (ud == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User author = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        var saved = postRepo.save(PostMapper.toEntity(dto, author));
        return ResponseEntity.status(HttpStatus.CREATED).body(PostMapper.toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll() {
        var list = postRepo.findAll().stream().map(PostMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable UUID id) {
        return postRepo.findById(id)
                .map(p -> ResponseEntity.ok(PostMapper.toDto(p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody PostRequest dto,
                                               @AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return postRepo.findById(id)
                .map(p -> {
                    p.setTitle(dto.getTitle());
                    p.setContent(dto.getContent());
                    p.setUpdatedDate(java.time.LocalDateTime.now());
                    return ResponseEntity.ok(PostMapper.toDto(postRepo.save(p)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (postRepo.existsById(id)) {
            postRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
