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
     * @param dto         данные для создании поста (title, content)
     * @param userDetails текущий аутентифицированный пользователь — автор поста
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
     * Получить пост по идентификатору.
     *
     * @param id идентификатор поста
     * @return найденный пост
     * @throws ConflictException если пост с таким id не найден
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
     * @param dto    новые данные поста (title, content)
     * @param ignore текущий пользователь (не используется напрямую, нужен для авторизации)
     * @return обновлённый пост; 404 если пост не найден
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
     * Удалить пост.
     *
     * @param id идентификатор поста
     * @return 204 No Content при успехе или 404, если пост не найден
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
     * Если {@code login} = {@code "admin"}, возвращаются все удалённые посты.
     * Иначе — только удалённые посты указанного пользователя.
     *
     * @param login логин пользователя либо строка {@code "admin"}
     * @return список удалённых постов
     */
    @GetMapping("/deleted")
    public ResponseEntity<List<PostResponse>> getDeletedPosts(@RequestParam String login) {

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
     * Если {@code login} = {@code "admin"}, возвращаются все активные посты.
     * Иначе — только активные посты указанного пользователя.
     *
     * @param login логин пользователя либо строка {@code "admin"}
     * @return список активных постов
     */
    @GetMapping("/active")
    public ResponseEntity<List<PostResponse>> getActivePosts(@RequestParam String login) {

        List<Post> posts = login.equals("admin")
                ? postRepo.findAllActive()
                : postRepo.findActiveByUsername(login);

        List<PostResponse> response = posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
