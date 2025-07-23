package org.example.backend.mapper;

import org.example.backend.dto.UserRequestDto;
import org.example.backend.dto.UserResponseDto;
import org.example.backend.model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public final class UserMapper {

    private static final BCryptPasswordEncoder ENC = new BCryptPasswordEncoder();
    private UserMapper() {}


    public static User toEntity(UserRequestDto dto) {
        User u = new User();
        copyIntoEntity(u, dto, true);
        return u;
    }


    public static void update(User u, UserRequestDto dto) {
        copyIntoEntity(u, dto, true);
        u.setUpdatedAt(LocalDateTime.now());
    }


    public static void patch(User u, UserRequestDto dto) {
        copyIntoEntity(u, dto, false);
        u.setUpdatedAt(LocalDateTime.now());
    }


    public static UserResponseDto toDto(User u) {
        UserResponseDto r = new UserResponseDto();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setBio(u.getBio());
        r.setAvatarUrl(u.getAvatarUrl());
        r.setBirthDate(u.getBirthDate());
        if (u.getGender() != null) r.setGender(u.getGender().name());
        r.setCreatedAt(u.getCreatedAt());
        r.setUpdatedAt(u.getUpdatedAt());
        return r;
    }


    private static void copyIntoEntity(User u, UserRequestDto d, boolean overwriteNull) {

        if (overwriteNull || d.getUsername()   != null) u.setUsername(d.getUsername());
        if (overwriteNull || d.getEmail()      != null) u.setEmail(d.getEmail());

        if (overwriteNull || d.getPassword()   != null) {
            String raw = d.getPassword();
            if (raw != null) u.setPasswordHash(ENC.encode(raw));
            else if (overwriteNull) u.setPasswordHash(null);
        }

        if (overwriteNull || d.getBio()        != null) u.setBio(d.getBio());
        if (overwriteNull || d.getAvatarUrl()  != null) u.setAvatarUrl(d.getAvatarUrl());
        if (overwriteNull || d.getBirthDate()  != null) u.setBirthDate(d.getBirthDate());

        if (overwriteNull || d.getGender()     != null) {
            if (d.getGender() != null)
                u.setGender(Gender.valueOf(d.getGender()));
            else if (overwriteNull)
                u.setGender(null);
        }
    }
}
