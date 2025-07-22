package org.example.backend.mapper;

import org.example.backend.dto.*;
import org.example.backend.model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public final class UserMapper {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private UserMapper() {}

    /* signup → entity */
    public static User toEntity(UserSignupDto dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setPasswordHash(encoder.encode(dto.getPassword()));
        u.setBio(dto.getBio());
        u.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getBirthDate() != null) u.setBirthDate(dto.getBirthDate());
        if (dto.getGender() != null)    u.setGender(Gender.valueOf(dto.getGender()));
        return u;
    }

    /* entity → response */
    public static UserResponseDto toDto(User u) {
        UserResponseDto d = new UserResponseDto();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setEmail(u.getEmail());
        d.setBio(u.getBio());
        d.setAvatarUrl(u.getAvatarUrl());
        d.setBirthDate(u.getBirthDate());
        if (u.getGender() != null) d.setGender(u.getGender().name());
        d.setCreatedAt(u.getCreatedAt());
        d.setUpdatedAt(u.getUpdatedAt());
        return d;
    }

    /* PATCH /api/users/me (частичное обновление) */
    public static void patch(User user, UserSignupDto dto) {
        if (dto.getBio()       != null) user.setBio(dto.getBio());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());
        if (dto.getGender()    != null) user.setGender(Gender.valueOf(dto.getGender()));
        user.setUpdatedAt(LocalDateTime.now());
    }
}
