package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Post;
import org.example.backend.model.User;
import org.example.backend.repository.PostRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepo;
    private final UserRepository userRepo;


    @PostMapping
    public ResponseEntity<Post> create(@Valid @RequestBody Post post,
                                       @AuthenticationPrincipal UserDetails ud) {

        User author = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        post.setAuthor(author);
        Post saved = postRepo.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @GetMapping
    public ResponseEntity<List<Post>> getAll() {
        return ResponseEntity.ok(postRepo.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable UUID id) {
        return postRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable UUID id,
                                       @Valid @RequestBody Post body) {

        return postRepo.findById(id)
                .map(p -> {
                    p.setTitle(body.getTitle());
                    p.setContent(body.getContent());
                    p.setUpdatedDate(LocalDateTime.now());
                    return ResponseEntity.ok(postRepo.save(p));
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
