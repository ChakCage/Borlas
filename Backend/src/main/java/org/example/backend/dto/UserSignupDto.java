// UserSignupDto.java  (POST /api/auth/signup)
package org.example.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSignupDto {

    /* username: 3‑30 символов латиницей, цифры, _‑ */
    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "only a-z, 0-9, _ and -")
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, max = 100)
    private String password;           // сырой пароль

    @Size(max = 500)
    private String bio;

    private String avatarUrl;

    @Past(message = "birthDate must be in the past")
    private LocalDate birthDate;

    @Pattern(regexp = "MALE|FEMALE", message = "gender must be MALE or FEMALE")
    private String gender;             // строкой, чтобы не ломать фронт
}
