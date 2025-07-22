// UserResponseDto.java (возвращаем наружу)
package org.example.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
    private LocalDate birthDate;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
