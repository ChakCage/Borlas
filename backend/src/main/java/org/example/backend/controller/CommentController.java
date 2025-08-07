package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.CommentRequest;
import org.example.backend.dto.CommentResponse;
import org.example.backend.dto.UnuversalOkResponce;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.model.*;
import org.example.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        // Находим пост по id, если нет — кидаем 404
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
        return ResponseEntity.ok(commentMapper.toDto(saved));
    }

    /**
     * Получить все комментарии к посту.
     *
     * @param postId идентификатор поста
     * @return список комментариев
     */
    @GetMapping("/post/{postId}")
    public List<CommentResponse> getCommentsByPost(@PathVariable UUID postId) {
        return commentRepository.findByPost_IdOrderByCreatedDateAsc(postId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить отдельный комментарий по id.
     *
     * @param id идентификатор комментария
     * @return комментарий
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable UUID id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(commentMapper.toDto(comment));
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
    public ResponseEntity<?> updateComment(@PathVariable UUID id,
                                           @RequestBody CommentRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Только автор может обновить свой комментарий!
        if (!comment.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        // Если новый текст пустой или состоит только из пробелов — считаем, что пользователь хочет удалить комментарий
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            comment.setDeletedDate(LocalDateTime.now());
            commentRepository.save(comment);

            // Возвращаем специальный ответ с сообщением, что комментарий был удалён
            var response = new UnuversalOkResponce(comment, "Комментарий был удалён, так как содержимое пустое.");

            return ResponseEntity.ok(response.getResponse());
        }

        // Обычное обновление комментария
        comment.setContent(request.getContent());
        comment.setEditedDate(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);

        return ResponseEntity.ok(commentMapper.toDto(saved));
    }

    /**
     * Удалить комментарий (может только автор).
     *
     * @param id идентификатор комментария
     * @param userDetails текущий пользователь (автор)
     * @return результат удаления
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Comment comment = commentRepository.findById(id).orElseThrow();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Только автор может удалить свой комментарий!
        if (!comment.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        comment.setDeletedDate(LocalDateTime.now());
        commentRepository.save(comment);
        return ResponseEntity.ok("Post with id: %s | Successfully Deleted");
    }
}
