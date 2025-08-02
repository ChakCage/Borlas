package org.example.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;


@Data
public class UserRequestDto {

    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "only a-z, 0-9, _ and -")
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, max = 100)
    private String password;

    @Size(max = 500)
    private String bio;
    private String avatarUrl;

    @Past
    private LocalDate birthDate;

    @Pattern(regexp = "MALE|FEMALE")
    private String gender;


}
