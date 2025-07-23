package org.example.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

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


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
