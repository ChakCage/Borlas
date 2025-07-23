package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.PostDto;
import org.example.backend.dto.PostResponseDto;
import org.example.backend.mapper.PostMapper;
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
    public ResponseEntity<PostResponseDto> create(@Valid @RequestBody PostDto dto,
                                                  @AuthenticationPrincipal UserDetails ud){
        User author = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        Post saved = postRepo.save(PostMapper.toEntity(dto, author));
        return ResponseEntity.status(HttpStatus.CREATED).body(PostMapper.toDto(saved));
    }

    @GetMapping
    public List<PostResponseDto> getAll() {
        return postRepo.findAll().stream().map(PostMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getById(@PathVariable UUID id){
        return postRepo.findById(id)
                .map(p->ResponseEntity.ok(PostMapper.toDto(p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> update(@PathVariable UUID id,
                                                  @Valid @RequestBody PostDto dto){
        return postRepo.findById(id)
                .map(p->{
                    PostMapper.update(p,dto);
                    return ResponseEntity.ok(PostMapper.toDto(postRepo.save(p)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        if(postRepo.existsById(id)){ postRepo.deleteById(id); return ResponseEntity.noContent().build();}
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
