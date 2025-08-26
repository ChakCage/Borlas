package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.PostRequest;
import org.example.backend.dto.PostResponse;
import org.example.backend.dto.UnuversalOkResponce;
import org.example.backend.mapper.PostMapper;
import org.example.backend.model.Post;
import org.example.backend.model.User;
import org.example.backend.repository.PostRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final PostMapper postMapper;

    /** Создать пост — data: объект, статус 201 */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody PostRequest dto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        User author = userRepo.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Post saved = postRepo.save(postMapper.toEntity(dto, author));

        var ok = new UnuversalOkResponce(
                postMapper.toDto(saved),
                "Пост создан",
                HttpStatus.CREATED.value() + " " + HttpStatus.CREATED.getReasonPhrase()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ok.getResponse());
    }

    /** Все посты — data: массив */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<PostResponse> postsList = postRepo.findAll()
                .stream().map(postMapper::toDto).collect(Collectors.toList());
        var ok = new UnuversalOkResponce(postsList, "Список постов получен", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /** Пост по id — data: объект */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        var ok = new UnuversalOkResponce(postMapper.toDto(post), "Пост получен", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /** Обновить — data: объект */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
                                                      @Valid @RequestBody PostRequest dto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        return postRepo.findById(id)
                .map(post -> {
                    post.setTitle(dto.getTitle());
                    post.setContent(dto.getContent());
                    post.setUpdatedDate(java.time.LocalDateTime.now());
                    Post saved = postRepo.save(post);
                    var ok = new UnuversalOkResponce(postMapper.toDto(saved), "Пост обновлён", "200 OK");
                    return ResponseEntity.ok(ok.getResponse());
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    /** Удалить — data: null (как и было) */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        if (!postRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        postRepo.deleteById(id);
        var ok = new UnuversalOkResponce(null, "Пост удалён", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /** Удалённые / активные — оставил как было (массив) */
    @GetMapping("/deleted")
    public ResponseEntity<Map<String, Object>> getDeletedPosts(@RequestParam String login) {
        List<Post> posts = login.equals("admin")
                ? postRepo.findAllDeleted()
                : postRepo.findDeletedByUsername(login);

        var ok = new UnuversalOkResponce(
                posts.stream().map(postMapper::toDto).collect(Collectors.toList()),
                "Удалённые посты получены", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActivePosts(@RequestParam String login) {
        List<Post> posts = login.equals("admin")
                ? postRepo.findAllActive()
                : postRepo.findActiveByUsername(login);

        var ok = new UnuversalOkResponce(
                posts.stream().map(postMapper::toDto).collect(Collectors.toList()),
                "Активные посты получены", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /** НОВОЕ: посты конкретного автора — data: массив */
    @GetMapping("/by-author/{userId}")
    public ResponseEntity<Map<String, Object>> getByAuthor(@PathVariable UUID userId) {
        List<Post> posts = postRepo.findAllByAuthorIdAndDeletedDateIsNullOrderByCreatedDateDesc(userId);
        var list = posts.stream().map(postMapper::toDto).toList();
        var ok = new UnuversalOkResponce(list, "Посты автора получены", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }
}
