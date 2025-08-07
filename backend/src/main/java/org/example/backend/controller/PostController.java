package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.PostRequest;
import org.example.backend.dto.PostResponse;
import org.example.backend.exception.ConflictException;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Контроллер для управления постами.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final PostMapper postMapper;

    /**
     * Создать новый пост.
     *
     * @param dto         данные поста
     * @param userDetails текущий пользователь (автор поста)
     * @return созданный пост
     */
    @PostMapping("/create")
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest dto,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User author = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        var saved = postRepo.save(postMapper.toEntity(dto, author));
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDto(saved));
    }

    /**
     * Получить список всех постов.
     *
     * @return список постов
     */
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll() {
        var list = postRepo.findAll().stream().map(postMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * Получить пост по id.
     *
     * @param id идентификатор поста
     * @return найденный пост или ошибка
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable UUID id) {
        try {
            return postRepo.findById(id)
                    .map(post -> ResponseEntity.ok(postMapper.toDto(post)))
                    .orElseThrow();
        } catch (Exception ex) {
            throw new ConflictException(String.format("Post with id %s not found", id));
        }
    }

    /**
     * Обновить пост.
     *
     * @param id     идентификатор поста
     * @param dto    новые данные поста
     * @param ignore текущий пользователь (нужен для срабатывания авторизации)
     * @return обновлённый пост
     */
    @PutMapping("update/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody PostRequest dto,
                                               @AuthenticationPrincipal UserDetails ignore) {
        return postRepo.findById(id)
                .map(post -> {
                    post.setTitle(dto.getTitle());
                    post.setContent(dto.getContent());
                    post.setUpdatedDate(java.time.LocalDateTime.now());
                    return ResponseEntity.ok(postMapper.toDto(postRepo.save(post)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Удалить пост по id.
     *
     * @param id идентификатор поста
     * @return результат удаления
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (postRepo.existsById(id)) {
            postRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Получить список удалённых постов.
     *
     * @param login имя пользователя или "admin"
     * @return список удалённых постов
     */
    @GetMapping("/deleted")
    public ResponseEntity<List<PostResponse>> getDeletedPosts(
            @RequestParam String login) {

        List<Post> posts = login.equals("admin")
                ? postRepo.findAllDeleted()
                : postRepo.findDeletedByUsername(login);

        List<PostResponse> response = posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Получить список активных (не удалённых) постов.
     *
     * @param login имя пользователя или "admin"
     * @return список активных постов
     */
    @GetMapping("/active")
    public ResponseEntity<List<PostResponse>> getActivePosts(
            @RequestParam String login) {

        List<Post> posts = login.equals("admin")
                ? postRepo.findAllActive()
                : postRepo.findActiveByUsername(login);

        List<PostResponse> response = posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
