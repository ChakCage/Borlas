package org.example.backend.repository;

import org.example.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPost_IdOrderByCreatedDateAsc(UUID postId);
}
