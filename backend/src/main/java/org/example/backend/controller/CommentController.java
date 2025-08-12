package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.CommentRequest;
import org.example.backend.dto.CommentResponse;
import org.example.backend.dto.UnuversalOkResponce;
import org.example.backend.exception.ConflictException;
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

    private static String s(HttpStatus st){ return st.value()+" "+st.getReasonPhrase(); }

    /**
     * Создать комментарий (или ответ на другой комментарий).
     */
    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            throw new ConflictException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован");

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
        var body = new UnuversalOkResponce(commentMapper.toDto(saved), "Комментарий создан", s(HttpStatus.CREATED)).getResponse();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Получить все комментарии к посту.
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPost(@PathVariable UUID postId) {
        List<CommentResponse> list = commentRepository.findByPost_IdOrderByCreatedDateAsc(postId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        var body = new UnuversalOkResponce(list, "Комментарии к посту", s(HttpStatus.OK)).getResponse();
        return ResponseEntity.ok(body);
    }

    /**
     * Получить отдельный комментарий по id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable UUID id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        var body = new UnuversalOkResponce(commentMapper.toDto(comment), "Комментарий найден", s(HttpStatus.OK)).getResponse();
        return ResponseEntity.ok(body);
    }

    /**
     * Обновить комментарий (только автор).
     * Пустой текст = «мягкое удаление» (deletedDate).
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@PathVariable UUID id,
                                           @RequestBody CommentRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            throw new ConflictException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован");

        Comment comment = commentRepository.findById(id).orElseThrow();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        if (!comment.getCreatedBy().getId().equals(user.getId()))
            throw new ConflictException(HttpStatus.FORBIDDEN, "Нельзя редактировать чужой комментарий");

        // пусто -> мягкое удаление
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            comment.setDeletedDate(LocalDateTime.now());
            commentRepository.save(comment);
            var body = new UnuversalOkResponce(null, "Комментарий удалён (пустой текст)", s(HttpStatus.OK)).getResponse();
            return ResponseEntity.ok(body);
        }

        comment.setContent(request.getContent());
        comment.setEditedDate(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);

        var body = new UnuversalOkResponce(commentMapper.toDto(saved), "Комментарий обновлён", s(HttpStatus.OK)).getResponse();
        return ResponseEntity.ok(body);
    }

    /**
     * Удалить комментарий (только автор, мягкое удаление).
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            throw new ConflictException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован");

        Comment comment = commentRepository.findById(id).orElseThrow();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        if (!comment.getCreatedBy().getId().equals(user.getId()))
            throw new ConflictException(HttpStatus.FORBIDDEN, "Нельзя удалять чужой комментарий");

        comment.setDeletedDate(LocalDateTime.now());
        commentRepository.save(comment);

        var body = new UnuversalOkResponce(null, "Комментарий удалён", s(HttpStatus.OK)).getResponse();
        return ResponseEntity.ok(body);
    }
}
