package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class CommentRequest {
    private UUID postId;

    @NotBlank(message = "content is required")
    private String content;

    private UUID parentCommentId; // для ответа на другой комментарий
}
