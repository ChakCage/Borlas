package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.CommentRequest;
import org.example.backend.dto.CommentResponse;
import org.example.backend.dto.UnuversalOkResponce;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.model.Comment;
import org.example.backend.model.Post;
import org.example.backend.model.User;
import org.example.backend.repository.CommentRepository;
import org.example.backend.repository.PostRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Контроллер для управления комментариями.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    /**
     * Создать комментарий (или ответ на другой комментарий).
     *
     * @param request DTO с данными комментария
     * @param userDetails текущий пользователь (автор комментария)
     * @return созданный комментарий
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createComment(@Valid @RequestBody CommentRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Comment parent = null;
        if (request.getParentCommentId() != null) {
            parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent comment not found"));
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setCreatedBy(user);
        comment.setContent(request.getContent());
        comment.setCreatedDate(LocalDateTime.now());
        comment.setParentComment(parent);

        Comment saved = commentRepository.save(comment);
        var ok = new UnuversalOkResponce(
                commentMapper.toDto(saved),
                "Комментарий создан",
                HttpStatus.CREATED.value() + " " + HttpStatus.CREATED.getReasonPhrase()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ok.getResponse());
    }

    /**
     * Получить все комментарии к посту.
     *
     * @param postId идентификатор поста
     * @return список комментариев
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getCommentsByPost(@PathVariable UUID postId) {
        List<CommentResponse> list = commentRepository.findByPost_IdOrderByCreatedDateAsc(postId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        var ok = new UnuversalOkResponce(list, "Комментарии получены", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Получить отдельный комментарий по id.
     *
     * @param id идентификатор комментария
     * @return комментарий
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getComment(@PathVariable UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        var ok = new UnuversalOkResponce(commentMapper.toDto(comment), "Комментарий получен", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Обновить комментарий (может только автор).
     * Если контент пустой — комментарий помечается удалённым.
     *
     * @param id идентификатор комментария
     * @param request новый текст комментария
     * @param userDetails текущий пользователь (автор)
     * @return обновлённый комментарий или сообщение об удалении
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateComment(@PathVariable UUID id,
                                                             @RequestBody CommentRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!comment.getCreatedBy().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can edit only your own comment");
        }

        // удаляем, если пусто
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            comment.setDeletedDate(LocalDateTime.now());
            commentRepository.save(comment);

            var response = new UnuversalOkResponce(
                    commentMapper.toDto(comment),
                    "Комментарий был удалён, так как содержимое пустое.",
                    "200 OK"
            );
            return ResponseEntity.ok(response.getResponse());
        }

        comment.setContent(request.getContent());
        comment.setEditedDate(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);

        var ok = new UnuversalOkResponce(commentMapper.toDto(saved), "Комментарий обновлён", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Удалить комментарий (может только автор).
     *
     * @param id идентификатор комментария
     * @param userDetails текущий пользователь (автор)
     * @return результат удаления
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable UUID id,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!comment.getCreatedBy().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can delete only your own comment");
        }

        comment.setDeletedDate(LocalDateTime.now());
        commentRepository.save(comment);

        var ok = new UnuversalOkResponce(null, "Комментарий помечен как удалён", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }
}
