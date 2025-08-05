package org.example.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PostRequest {
    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "content is required")
    private String content;

    private Boolean isDraft;
}