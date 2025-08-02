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
        User user = new User();
        copyIntoEntity(user, dto, true);
        return user;
    }


    public static void update(User user, UserRequestDto dto) {
        copyIntoEntity(user, dto, true);
        user.setUpdatedAt(LocalDateTime.now());
    }


    public static void patch(User user, UserRequestDto dto) {
        copyIntoEntity(user, dto, false);
        user.setUpdatedAt(LocalDateTime.now());
    }


    public static UserResponseDto toDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setBio(user.getBio());
        userResponseDto.setAvatarUrl(user.getAvatarUrl());
        userResponseDto.setBirthDate(user.getBirthDate());
        if (user.getGender() != null) userResponseDto.setGender(user.getGender().name());
        userResponseDto.setCreatedAt(user.getCreatedAt());
        userResponseDto.setUpdatedAt(user.getUpdatedAt());
        return userResponseDto;
    }


    private static void copyIntoEntity(User user, UserRequestDto dto, boolean overwriteNull) {

        if (overwriteNull || dto.getUsername()   != null) user.setUsername(dto.getUsername());
        if (overwriteNull || dto.getEmail()      != null) user.setEmail(dto.getEmail());

        if (overwriteNull || dto.getPassword()   != null) {
            String raw = dto.getPassword();
            if (raw != null) user.setPasswordHash(ENC.encode(raw));
            else if (overwriteNull) user.setPasswordHash(null);
        }

        if (overwriteNull || dto.getBio()        != null) user.setBio(dto.getBio());
        if (overwriteNull || dto.getAvatarUrl()  != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (overwriteNull || dto.getBirthDate()  != null) user.setBirthDate(dto.getBirthDate());

        if (overwriteNull || dto.getGender()     != null) {
            if (dto.getGender() != null)
                user.setGender(Gender.valueOf(dto.getGender()));
            else if (overwriteNull)
                user.setGender(null);
        }
    }
}
