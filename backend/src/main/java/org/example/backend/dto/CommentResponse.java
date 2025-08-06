package org.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentResponse {
    private UUID id;
    private String content;
    private UUID postId;
    private UUID createdBy;
    private String createdByName;
    private LocalDateTime createdDate;
    private LocalDateTime editedDate;
    private LocalDateTime deletedDate;
    private UUID parentCommentId;
}
