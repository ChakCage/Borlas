package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "edited_date")
    private LocalDateTime editedDate;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;
}
