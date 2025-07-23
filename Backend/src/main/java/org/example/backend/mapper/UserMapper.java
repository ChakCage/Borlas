package org.example.backend.mapper;

import org.example.backend.dto.*;
import org.example.backend.model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;





public final class UserMapper {

    private static final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    private UserMapper() {}

    /* ───────── signup / create ───────── */
    public static User toEntity(UserDto d) {                 // для create
        User u = new User();
        u.setUsername(d.getUsername());
        u.setEmail(d.getEmail());
        u.setPasswordHash(enc.encode(d.getPassword()));
        return u;
    }

    public static User toEntity(UserSignupDto d) {           // для signup
        User u = new User();
        u.setUsername(d.getUsername());
        u.setEmail(d.getEmail());
        u.setPasswordHash(enc.encode(d.getPassword()));
        u.setBio(d.getBio());
        u.setAvatarUrl(d.getAvatarUrl());
        if (d.getBirthDate()!=null) u.setBirthDate(d.getBirthDate());
        if (d.getGender()!=null)    u.setGender(Gender.valueOf(d.getGender()));
        return u;
    }

    /* ───────── PUT full update ───────── */
    public static void update(User u, UserDto d){
        u.setUsername(d.getUsername());
        u.setEmail(d.getEmail());
        u.setPasswordHash(enc.encode(d.getPassword()));
        u.setUpdatedAt(LocalDateTime.now());
    }

    /* ───────── PATCH profile ─────────── */
    public static void patch(User u, UserPatchDto d){
        if (d.getBio()!=null)        u.setBio(d.getBio());
        if (d.getAvatarUrl()!=null)  u.setAvatarUrl(d.getAvatarUrl());
        if (d.getBirthDate()!=null)  u.setBirthDate(d.getBirthDate());
        if (d.getGender()!=null)     u.setGender(Gender.valueOf(d.getGender()));
        u.setUpdatedAt(LocalDateTime.now());
    }

    /* ───────── entity → response ─────── */
    public static UserResponseDto toDto(User u){
        UserResponseDto r = new UserResponseDto();
        r.setId(u.getId()); r.setUsername(u.getUsername()); r.setEmail(u.getEmail());
        r.setBio(u.getBio()); r.setAvatarUrl(u.getAvatarUrl());
        r.setBirthDate(u.getBirthDate());
        if (u.getGender()!=null) r.setGender(u.getGender().name());
        r.setCreatedAt(u.getCreatedAt()); r.setUpdatedAt(u.getUpdatedAt());
        return r;
    }
}

