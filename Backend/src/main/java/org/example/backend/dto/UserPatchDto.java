package org.example.backend.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserPatchDto {
    @Size(max = 500)         private String bio;
    private String avatarUrl;
    @Past                     private LocalDate birthDate;
    @Pattern(regexp="MALE|FEMALE") private String gender;
}