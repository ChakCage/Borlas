package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostDto {
    @NotBlank
    @Size(max = 255) private String title;
    @NotBlank                   private String content;
}