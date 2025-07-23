package org.example.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank @Size(min = 3, max = 30) @Pattern(regexp="^[a-z0-9_-]+$")
    private String username;
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6, max = 100)
    private String password;
}