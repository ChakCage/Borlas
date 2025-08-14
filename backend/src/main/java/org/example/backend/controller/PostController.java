package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.PostRequest;
import org.example.backend.dto.PostResponse;
import org.example.backend.dto.UnuversalOkResponce;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
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
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody PostRequest dto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        User author = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        var saved = postRepo.save(postMapper.toEntity(dto, author));

        var ok = new UnuversalOkResponce(
                List.of(postMapper.toDto(saved)),
                "Пост создан",
                HttpStatus.CREATED.value() + " " + HttpStatus.CREATED.getReasonPhrase()
        );
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Получить список всех постов.
     *
     * @return список постов
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<PostResponse> postsList = postRepo.findAll().stream().map(postMapper::toDto).collect(Collectors.toList());
        var unuversalOkResponce = new UnuversalOkResponce(postsList, "Список постов получен", "200 OK");
        return ResponseEntity.ok(unuversalOkResponce.getResponse());
    }

    /**
     * Получить пост по id.
     *
     * @param id идентификатор поста
     * @return найденный пост или ошибка
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        var post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Post with id %s not found", id)));

        var unuversalOkResponce = new UnuversalOkResponce(List.of(postMapper.toDto(post)), "Пост получен", "200 OK");
        return ResponseEntity.ok(unuversalOkResponce.getResponse());
    }

    /**
     * Обновить пост.
     *
     * @param id     идентификатор поста
     * @param dto    новые данные поста
     * @param userDetails текущий пользователь (чтобы сработала авторизация)
     * @return обновлённый пост
     */
    @PutMapping("update/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
                                                      @Valid @RequestBody PostRequest dto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return postRepo.findById(id)
                .map(post -> {
                    post.setTitle(dto.getTitle());
                    post.setContent(dto.getContent());
                    post.setUpdatedDate(java.time.LocalDateTime.now());
                    var saved = postRepo.save(post);
                    var ok = new UnuversalOkResponce(List.of(postMapper.toDto(saved)), "Пост обновлён", "200 OK");
                    return ResponseEntity.ok(ok.getResponse());
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Post with id %s not found", id)));
    }

    /**
     * Удалить пост по id.
     *
     * @param id идентификатор поста
     * @return результат удаления
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        if (!postRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Post with id %s not found", id));
        }
        postRepo.deleteById(id);
        var ok = new UnuversalOkResponce(null, "Пост удалён", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Получить список удалённых постов.
     *
     * @param login имя пользователя или "admin"
     * @return список удалённых постов
     */
    @GetMapping("/deleted")
    public ResponseEntity<Map<String, Object>> getDeletedPosts(@RequestParam String login) {
        List<Post> posts = login.equals("admin")
                ? postRepo.findAllDeleted()
                : postRepo.findDeletedByUsername(login);

        var ok = new UnuversalOkResponce(
                posts.stream().map(postMapper::toDto).collect(Collectors.toList()),
                "Удалённые посты получены",
                "200 OK"
        );
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Получить список активных (не удалённых) постов.
     *
     * @param login имя пользователя или "admin"
     * @return список активных постов
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActivePosts(@RequestParam String login) {
        List<Post> posts = login.equals("admin")
                ? postRepo.findAllActive()
                : postRepo.findActiveByUsername(login);

        var ok = new UnuversalOkResponce(
                posts.stream().map(postMapper::toDto).collect(Collectors.toList()),
                "Активные посты получены",
                "200 OK"
        );
        return ResponseEntity.ok(ok.getResponse());
    }
}
