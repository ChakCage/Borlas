package org.example.backend.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;
}